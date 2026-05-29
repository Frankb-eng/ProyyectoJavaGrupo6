package org.tallerjava.ModuloCarga.Interface.remota.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloCarga.aplicacion.ServicioCarga;
import org.tallerjava.ModuloCarga.dominio.Carga;
import org.tallerjava.ModuloCarga.dominio.EstacionCarga;
import org.tallerjava.ModuloCarga.Interface.remota.rest.dto.*;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Path("/cargas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CargaAPI {

    @Inject
    ServicioCarga servicioCarga;

    // curl -X POST http://localhost:8080/TallerJavaEquipo6/api/cargas/estaciones
    // -H "Content-Type: application/json"
    // -d '{"descripcion":"Estacion Centro","calle":"18 de Julio","departamento":"Montevideo","longitud":-34,"latitud":-56}'
    @POST
    @Path("/estaciones")
    public Response altaEstacion(EstacionDTO dto) {
        try {
            EstacionCarga estacion = dto.build();
            long id = servicioCarga.altaEstacion(estacion);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // curl -X GET http://localhost:8080/TallerJavaEquipo6/api/cargas/estaciones
    @GET
    @Path("/estaciones")
    public Response obtenerEstaciones() {
        List<EstacionCarga> estaciones = servicioCarga.obtenerEstaciones();
        return Response.ok(estaciones).build();
    }

    // curl -X POST http://localhost:8080/TallerJavaEquipo6/api/cargas/cargadores
    // -H "Content-Type: application/json"
    // -d '{"idEstacion":1,"tipo":"RAPIDO","tieneCable":true,"tipoConector":"TIPO2","potenciaMinima":22}'
    @POST
    @Path("/cargadores")
    public Response altaCargador(CargadorDTO dto) {
        try {
            long id = servicioCarga.altaCargador(dto.getIdEstacion(), dto.build());
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // curl -X POST http://localhost:8080/TallerJavaEquipo6/api/cargas/iniciar
    // -H "Content-Type: application/json"
    // -d '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":1}'
    @POST
    @Path("/iniciar")
    public Response iniciarCarga(IniciarCargaDTO dto) {
        try {
            long idCarga = servicioCarga.iniciarCarga(
                    dto.getCedulaCliente(), dto.getIdCargador(), dto.getIdMedioPago());
            return Response.status(Response.Status.CREATED).entity(idCarga).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // curl -X POST http://localhost:8080/TallerJavaEquipo6/api/cargas/finalizar
    // -H "Content-Type: application/json"
    // -d '{"idCargador":1,"consumoKwh":15.5,"minutosDemora":0}'
    @POST
    @Path("/finalizar")
    public Response finalizarCarga(FinalizarCargaDTO dto) {
        try {
            servicioCarga.finalizarCarga(dto.getIdCargador(), dto.getConsumoKwh(), dto.getMinutosDemora());
            return Response.ok("Carga finalizada correctamente").build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // curl -X GET "http://localhost:8080/TallerJavaEquipo6/api/cargas/activa?cedulaCliente=12345678"
    @GET
    @Path("/activa")
    public Response verCargaActual(@QueryParam("cedulaCliente") String cedulaCliente) {
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

    // curl -X GET "http://localhost:8080/TallerJavaEquipo6/api/cargas/historico?cedulaCliente=12345678&fechaIni=2026-01-01&fechaFin=2026-12-31"
    @GET
    @Path("/historico")
    public Response verHistorico(
            @QueryParam("cedulaCliente") String cedulaCliente,
            @QueryParam("fechaIni") String fechaIni,
            @QueryParam("fechaFin") String fechaFin) {
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