package org.tallerjava.ModuloCliente.aplicacion.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.tallerjava.ModuloCliente.Interface.evento.out.PublicadorEventoCliente;
import org.tallerjava.ModuloCliente.Interface.evento.out.PublicadorEventoMedioPago;
import org.tallerjava.ModuloCliente.Interface.local.InterfaceLocalCliente;
import org.tallerjava.ModuloCliente.aplicacion.ServicioClientes;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.ClienteProfesional;
import org.tallerjava.ModuloCliente.dominio.MedioPago;
import org.tallerjava.ModuloCliente.dominio.Reclamo;
import org.tallerjava.ModuloCliente.dominio.Tarjeta;
import org.tallerjava.ModuloCliente.dominio.repositorio.ClienteRepositorio;


import java.util.List;


@ApplicationScoped
@Transactional
public class ServicioClientesImpl implements ServicioClientes, InterfaceLocalCliente {

    @Inject
    ClienteRepositorio clienteRepositorio;

    @Inject
    PublicadorEventoMedioPago publicadorEventoMedioPago;

    @Inject
    PublicadorEventoCliente publicadorEventoCliente;

    public ServicioClientesImpl() {}

    @Override
    public void registrarCliente(Cliente cliente) {

        if(existeCliente(cliente.getCedula())){
            throw new IllegalStateException(
            "Ya existe un cliente registrado con la cédula: " + cliente.getCedula());
        }

        String contrasenaHasheada = BCrypt.hashpw(cliente.getContrasena(), BCrypt.gensalt());
        cliente.setContrasena(contrasenaHasheada);
        clienteRepositorio.guardar(cliente);

        // avisa a Cargas y Pagos que existe este cliente
        publicadorEventoCliente.publicar(
                cliente.getCedula(),
                cliente.getNombreCompleto(),
                cliente instanceof ClienteProfesional ? "PROFESIONAL" : "COMUN"
        );
    }

    @Override
    public void altaMedioPago(String cedula, MedioPago medioPago) {
        Cliente cliente = obtenerClienteOFallar(cedula);
        cliente.agregarMedioPago(medioPago);
        clienteRepositorio.actualizar(cliente);
        

        publicadorEventoMedioPago.publicar(
            cedula,
            medioPago.getId(),
            medioPago instanceof Tarjeta ? "TARJETA" : "UTE",
            medioPago.esPredeterminado()
        );

    }

    @Override
    public List<Cliente> obtenerClientes() {
        return clienteRepositorio.obtenerTodos();
    }

    @Override
    public void realizarReclamo(String cedula, String comentario) {
        Cliente cliente = obtenerClienteOFallar(cedula);
        cliente.agregarReclamo(new Reclamo(comentario, cedula));
        clienteRepositorio.actualizar(cliente);
    }

    @Override
    public Cliente buscarPorCedula(String cedula) {
        return clienteRepositorio.buscarPorCedula(cedula);
    }

    @Override
    public boolean existeCliente(String cedula){
        if (clienteRepositorio.buscarPorCedula(cedula) != null) {
            return true;
        } else {
            return false;

        }
    }

    // metodos privados

    private Cliente obtenerClienteOFallar(String cedula) {

        if(existeCliente(cedula)){
            return clienteRepositorio.buscarPorCedula(cedula);
        }else{
            throw new IllegalArgumentException(
                    "No existe cliente registrado con la cédula: " + cedula);
        }

    }
}
