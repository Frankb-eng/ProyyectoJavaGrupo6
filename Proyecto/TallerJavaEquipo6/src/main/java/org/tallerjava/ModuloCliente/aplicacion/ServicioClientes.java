package org.tallerjava.ModuloCliente.aplicacion;

import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.MedioPago;

import java.util.List;


public interface ServicioClientes {

    void registrarCliente(Cliente cliente);

    void altaMedioPago(String cedula, MedioPago medioPago);

    List<Cliente> obtenerClientes();

    void realizarReclamo(String cedula, String comentario);

    /**
     * Clasifica el sentimiento de un reclamo ya persistido y guarda el resultado
     * en su campo etiqueta. Lo invoca el consumidor JMS al recibir el mensaje
     * de la queue de reclamos.
     */
    void clasificarReclamo(Long idReclamo);

    /**
     * Variante sincronica de realizarReclamo: persiste el reclamo y lo
     * clasifica via LLM en la misma llamada, sin pasar por la queue.
     * Existe unicamente para comparar, con JMeter, la latencia del flujo
     * sincronico contra el flujo asincronico (ver realizarReclamo).
     */
    void realizarReclamoSincro(String cedula, String comentario);

    Cliente buscarPorCedula(String cedula);

    boolean existeCliente(String cedula);
}