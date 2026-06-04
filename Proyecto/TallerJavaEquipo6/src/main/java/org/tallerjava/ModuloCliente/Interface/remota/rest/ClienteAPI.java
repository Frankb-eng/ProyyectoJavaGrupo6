package org.tallerjava.ModuloCliente.Interface.remota.rest;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import java.util.*;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;


import org.tallerjava.ModuloCliente.aplicacion.ServicioClientes;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.MedioPago;
import org.tallerjava.ModuloCliente.Interface.remota.rest.dto.*;

@DenyAll
//por defecto ningun endpoint es accesible
// cada metodo debe declarar explicitamente quien puede acceder
@Path("/clientes")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteAPI {

    @Inject
    ServicioClientes servicioClientes;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/registrar")
    @PermitAll // publico, cualquiera puede registrarse
    public Response registrar(ClienteDTO dto) {
        try {
            Cliente cliente = dto.build();
            servicioClientes.registrarCliente(cliente);
            return Response.status(Response.Status.CREATED).entity(cliente.getCedula()).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{cedula}/medioPago")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response agregarMedioPago(@PathParam("cedula") String cedula, MedioPagoDTO dto) {

        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();

        // verificamos que sea el mismo que el del path
        if (!cedulaAutenticada.equals(cedula)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permiso para hacer esto")
                    .build();
        }

        try {
            MedioPago medioPago = dto.build();
            servicioClientes.altaMedioPago(cedula, medioPago);
            return Response.ok().entity("Medio de pago agregado correctamente").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @PermitAll // endpoint del Gestor Web, acceso publico
    public Response obtenerClientes() {
        List<Cliente> clientes = servicioClientes.obtenerClientes();
        List<ClienteDTO> clientesDTO = new ArrayList<>();
        for (Cliente cliente : clientes) {
            clientesDTO.add(ClienteDTO.convertirDTO(cliente));
        }
        return Response.ok(clientesDTO).build();
    }

    @POST
    @Path("/{cedula}/reclamos")
    @RolesAllowed("CLIENTE") // endpoint de App Movil, requiere autenticacion
    public Response realizarReclamo(@PathParam("cedula") String cedula, ReclamoDTO dto) {
        final String cedulaAutenticada = securityContext.getUserPrincipal().getName();
        if (!cedulaAutenticada.equals(cedula)) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tiene permiso para hacer esto").build();
                            
        }
        
        
        
        try {
            servicioClientes.realizarReclamo(cedula, dto.getComentario());
            return Response.status(Response.Status.CREATED).entity("Reclamo registrado correctamente").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}