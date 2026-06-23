package org.tallerjava.ModuloCliente.aplicacion.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.tallerjava.ModuloCliente.Interface.evento.out.PublicadorEventoCliente;
import org.tallerjava.ModuloCliente.Interface.evento.out.PublicadorEventoMedioPago;
import org.tallerjava.ModuloCliente.Interface.evento.out.PublicadorEventoReclamo;
import org.tallerjava.ModuloCliente.Interface.local.InterfaceLocalCliente;
import org.tallerjava.ModuloCliente.aplicacion.ServicioClientes;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.ClienteProfesional;
import org.tallerjava.ModuloCliente.dominio.EstadoReclamo;
import org.tallerjava.ModuloCliente.dominio.MedioPago;
import org.tallerjava.ModuloCliente.dominio.Reclamo;
import org.tallerjava.ModuloCliente.dominio.Tarjeta;
import org.tallerjava.ModuloCliente.dominio.repositorio.ClienteRepositorio;
import org.tallerjava.ModuloCliente.dominio.repositorio.ReclamoRepositorio;
import org.tallerjava.ModuloCliente.infraestructura.llm.ClasificadorSentimientoLlamaClient;
import org.tallerjava.ModuloCliente.infraestructura.messaging.EnviarReclamoQueueUtil;
import org.tallerjava.ModuloCliente.infraestructura.messaging.ReclamoMessage;


import java.util.List;


@ApplicationScoped
@Transactional
public class ServicioClientesImpl implements ServicioClientes, InterfaceLocalCliente {

    @Inject
    ClienteRepositorio clienteRepositorio;

    @Inject
    ReclamoRepositorio reclamoRepositorio;

    @Inject
    PublicadorEventoMedioPago publicadorEventoMedioPago;

    @Inject
    PublicadorEventoCliente publicadorEventoCliente;

    @Inject
    EnviarReclamoQueueUtil enviarReclamoQueueUtil;

    @Inject
    ClasificadorSentimientoLlamaClient clasificadorSentimientoLlamaClient;

    @Inject
    PublicadorEventoReclamo publicadorEventoReclamo;

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

        Reclamo reclamo = new Reclamo(comentario, cedula);
        cliente.agregarReclamo(reclamo);
        clienteRepositorio.actualizar(cliente);
        enviarReclamoQueueUtil.enviarReclamo(
                new ReclamoMessage(reclamo.getId(), reclamo.getComentario(), cedula)
        );
    }

    @Override
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void clasificarReclamo(Long idReclamo) {
        // NOT_SUPPORTED: la llamada al LLM puede demorar hasta varios minutos
        // (ver ClasificadorSentimientoLlamaClient), por lo que esta operacion
        // se ejecuta deliberadamente fuera de una transaccion JTA para no
        // mantener una transaccion abierta (y locks de fila) durante ese tiempo.
        // reclamoRepositorio.buscarPorId/actualizar son invocados sobre otro bean
        // CDI (ReclamoRepositorioImpl), que abre su propia transaccion corta.
        Reclamo reclamo = reclamoRepositorio.buscarPorId(idReclamo);
        if (reclamo == null) {
            // el reclamo pudo haber sido eliminado entre que se encolo el mensaje y se proceso
            return;
        }

        EstadoReclamo etiqueta = clasificadorSentimientoLlamaClient.clasificar(reclamo.getComentario());
        reclamo.setEtiqueta(etiqueta);
        reclamoRepositorio.actualizar(reclamo);
        publicadorEventoReclamo.publicarReclamoClasificado(idReclamo, etiqueta);
    }

    @Override
    public void realizarReclamoSincro(String cedula, String comentario) {
        Cliente cliente = obtenerClienteOFallar(cedula);
        Reclamo reclamo = new Reclamo(comentario, cedula);
        cliente.agregarReclamo(reclamo);
        clienteRepositorio.actualizar(cliente);
        // sin pasar por la queue — esto es lo que se mide contra realizarReclamo()
        // en el plan de pruebas JMeter (paso 7)
        clasificarReclamo(reclamo.getId());
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

    private Cliente obtenerClienteOFallar(String cedula) {

        if(existeCliente(cedula)){
            return clienteRepositorio.buscarPorCedula(cedula);
        }else{
            throw new IllegalArgumentException(
                    "No existe cliente registrado con la cédula: " + cedula);
        }

    }
}