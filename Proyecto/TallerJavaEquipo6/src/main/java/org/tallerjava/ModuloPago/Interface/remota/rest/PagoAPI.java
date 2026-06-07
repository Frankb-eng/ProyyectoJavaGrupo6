package org.tallerjava.ModuloPago.Interface.remota.rest;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloPago.Interface.remota.rest.dto.PagoDTO;
import org.tallerjava.ModuloPago.aplicacion.ServicioPago;
import org.tallerjava.ModuloPago.dominio.EstadoPago;
import org.tallerjava.ModuloPago.dominio.Pago;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@DenyAll
// por defecto ningun endpoint es accesible
// cada metodo debe declarar explicitamente quien puede acceder
@Path("/pagos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoAPI {

    @Inject
    ServicioPago servicioPago;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/{cedula}/listarPagos")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response consultarPagos(
            @PathParam("cedula") String cedula,
            @QueryParam("fechaIni") String fechaIni,
            @QueryParam("fechaFin") String fechaFin) {

        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();
        if (!cedulaAutenticada.equals(cedula)) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tiene permiso para hacer esto").build();
                    
                    
        }
        LocalDate ini = LocalDate.parse(fechaIni);
        LocalDate fin = LocalDate.parse(fechaFin);
        List<Pago> pagos = servicioPago.consultarPagos(cedula, ini, fin);
        return Response.ok(pagos).build();
    }
}