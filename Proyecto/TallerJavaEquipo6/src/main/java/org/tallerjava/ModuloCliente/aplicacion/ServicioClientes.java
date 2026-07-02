package org.tallerjava.ModuloCliente.aplicacion;

import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.MedioPago;

import java.util.List;


public interface ServicioClientes {

    void registrarCliente(Cliente cliente);

    void altaMedioPago(String cedula, MedioPago medioPago);

    List<Cliente> obtenerClientes();

    void realizarReclamo(String cedula, String comentario);

    void clasificarReclamo(Long idReclamo);

    void realizarReclamoSincro(String cedula, String comentario);

    Cliente buscarPorCedula(String cedula);

    boolean existeCliente(String cedula);
}