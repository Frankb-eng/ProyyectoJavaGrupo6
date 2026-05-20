package org.tallerjava.ModuloPago.Interface.remota.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloPago.Interface.remota.rest.dto.PagoDTO;
import org.tallerjava.ModuloPago.aplicacion.ServicioPago;
import org.tallerjava.ModuloPago.dominio.EstadoPago;
import org.tallerjava.ModuloPago.dominio.Pago;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Path("/pagos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoAPI {

    @Inject
    ServicioPago servicioPago;

    @GET
    @Path("/{cedula}/listarPagos")
    public Response consultarPagos(
            @PathParam("cedula") String cedula,
            @QueryParam("fechaIni") String fechaIni,
            @QueryParam("fechaFin") String fechaFin) {

            LocalDate ini = LocalDate.parse(fechaIni);
            LocalDate fin = LocalDate.parse(fechaFin);

            List<Pago> pagos = servicioPago.consultarPagos(cedula, ini, fin);

        return Response.ok(pagos).build();
    }

    @POST
    @Path("/{cedula}/pagar")
    public Response pagarCarga(@PathParam("cedula") String cedula, PagoDTO dto){

        try {
            Pago pago = dto.build();
            pago.setEstado(EstadoPago.COMPLETADO);
            servicioPago.altaPago(cedula, pago);
            return Response.ok().entity("Carga pagada correctamente").build();


        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        }
    }
}
