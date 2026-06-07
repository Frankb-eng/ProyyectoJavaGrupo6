package org.tallerjava.ModuloCarga.Interface.remota.rest;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloCarga.aplicacion.ServicioCarga;
import org.tallerjava.ModuloCarga.dominio.Carga;
import org.tallerjava.ModuloCarga.dominio.EstacionCarga;
import org.tallerjava.ModuloCarga.Interface.remota.rest.dto.*;
import org.tallerjava.seguridad.RateLimiter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.List;

@DenyAll
// buena practica: por defecto ningun endpoint es accesible
// cada metodo debe declarar explicitamente quien puede acceder
@ApplicationScoped
@Path("/cargas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CargaAPI {

    @Inject
    ServicioCarga servicioCarga;

    @Inject
    RateLimiter rateLimiter;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/estaciones")
    @PermitAll // endpoint del Gestor Web, acceso publico
    public Response altaEstacion(EstacionDTO dto) {
        try {
            EstacionCarga estacion = dto.build();
            long id = servicioCarga.altaEstacion(estacion);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/estaciones")
    @PermitAll // endpoint del Gestor Web, acceso publico
    public Response obtenerEstaciones() {
        List<EstacionCarga> estaciones = servicioCarga.obtenerEstaciones();
        return Response.ok(estaciones).build();
    }

    @POST
    @Path("/cargadores")
    @PermitAll // endpoint del Gestor Web, acceso publico
    public Response altaCargador(CargadorDTO dto) {
        try {
            long id = servicioCarga.altaCargador(dto.getIdEstacion(), dto.build());
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/iniciar")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response iniciarCarga(IniciarCargaDTO dto) {

        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();
        if (!cedulaAutenticada.equals(dto.getCedulaCliente())) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tiene permiso para operar sobre este recurso.").build();
                         
        }

        try {
            long idCarga = servicioCarga.iniciarCarga(
                    dto.getCedulaCliente(), dto.getIdCargador(), dto.getIdMedioPago());
            return Response.status(Response.Status.CREATED).entity(idCarga).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/finalizar")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response finalizarCarga(FinalizarCargaDTO dto) {
        try {
            servicioCarga.finalizarCarga(dto.getIdCargador(), dto.getConsumoKwh(), dto.getMinutosDemora());
            return Response.ok("Carga finalizada. Verifique el estado del pago en el historial.").build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/activa")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response verCargaActual(@QueryParam("cedulaCliente") String cedulaCliente) {
        
        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();
        if (!cedulaAutenticada.equals(cedulaCliente)) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tiene permiso para hacer esto").build();
                    
                    
        }
        
        
        try {
            Carga carga = servicioCarga.verCargaActual(cedulaCliente);
            if (carga == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No hay carga activa para el cliente").build();
            }
            return Response.ok(carga).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/historico")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response verHistorico(
            @QueryParam("cedulaCliente") String cedulaCliente,
            @QueryParam("fechaIni") String fechaIni,
            @QueryParam("fechaFin") String fechaFin) {


        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();
        if (!cedulaAutenticada.equals(cedulaCliente)) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tiene permiso para hacer esto").build();
                    
                    
        }

        // verifica rate limit antes de procesar
        if (!rateLimiter.consumir()) {
            return Response.status(429)
                    .entity("Limite de consultas excedido. Intente nuevamente en unos segundos.")
                    .build();
        }

        try {
            List<Carga> historico = servicioCarga.verHistorico(
                    cedulaCliente,
                    LocalDate.parse(fechaIni),
                    LocalDate.parse(fechaFin));
            return Response.ok(historico).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}