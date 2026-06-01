package org.tallerjava.ModuloPago.aplicacion.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.repositorio.ClienteRepositorio;
import org.tallerjava.ModuloPago.Interface.local.InterfaceLocalPago;
import org.tallerjava.ModuloPago.Interface.remota.rest.dto.NotificacionDTO;
import org.tallerjava.ModuloPago.aplicacion.ServicioPago;
import org.tallerjava.ModuloPago.dominio.EstadoPago;
import org.tallerjava.ModuloPago.dominio.MedioPagoPago;
import org.tallerjava.ModuloPago.dominio.Pago;
import org.tallerjava.ModuloPago.dominio.ClientePago;
import org.tallerjava.ModuloPago.dominio.repositorio.ClientePagoRepositorio;
import org.tallerjava.ModuloPago.dominio.repositorio.MedioPagoPagoRepositorio;
import org.tallerjava.ModuloPago.dominio.repositorio.PagoRepositorio;

import java.time.LocalDate;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional
public class ServicioPagoImpl implements ServicioPago, InterfaceLocalPago
{
    @Inject
    PagoRepositorio pagoRepositorio;

    @Inject
    ClienteRepositorio clienteRepositorio;

    @Inject
    MedioPagoPagoRepositorio medioPagoPagoRepositorio;

    @Inject
    ClientePagoRepositorio clientePagoRepositorio;

    private static final Logger log = Logger.getLogger(ServicioPagoImpl.class);

    @Override
    public boolean altaPago(String cedula, Pago pago) {

        boolean pagoAprobado = false;
        try (Client client = ClientBuilder.newClient()) {
            MedioPagoPago mpp = medioPagoPagoRepositorio.buscarPorIdMedioPago(pago.getIdMedioPago());

            NotificacionDTO nDto = new NotificacionDTO();
            nDto.setMonto(pago.getImporte());
            nDto.setPago(mpp);

            String url;
            if ("TARJETA".equals(mpp.getTipoMedioPago())) {
                url = "http://localhost:8080/APIPago/api/pagos/autorizar";
            } else {
                url = "http://localhost:8080/APIPago/api/ute/notificarPago";
            }

            Response response = client
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(nDto, MediaType.APPLICATION_JSON));

            pagoAprobado = Boolean.TRUE.equals(response.readEntity(Boolean.class));

        } catch (Exception e) {
            log.warnf("API externa no disponible, simulando rechazo: %s", e.getMessage());
            pagoAprobado = false;
        }

        pago.setEstado(pagoAprobado ? EstadoPago.COMPLETADO : EstadoPago.RECHAZADO);
        pagoRepositorio.guardar(pago);

        Cliente c = clienteRepositorio.buscarPorCedula(cedula);
        c.agregarPago(pago);
        clienteRepositorio.actualizar(c);

        if (!pagoAprobado) {
            bloquearCliente(cedula);
        }

        return pagoAprobado;
    }

    public List<Pago> consultarPagos(String cedula, LocalDate fechaIni, LocalDate fechaFin){ return pagoRepositorio.listarPagosPorCedulaYFechas(cedula,fechaIni,fechaFin); }

    public boolean pagarCarga(String cedula, int importe, Long idMedioPago){
        Pago pago = new Pago(cedula,importe,idMedioPago);
        return this.altaPago(cedula, pago);
    }

    @Override
    public boolean clienteHabilitadoParaCargar(String cedula) {
        ClientePago clientePago = clientePagoRepositorio.buscarPorCedula(cedula);
        if (clientePago == null) {
            log.warnf("Cliente %s no encontrado en ModuloPago, se permite la carga.", cedula);
            return true;
        }
        return !clientePago.isBloqueadoPorDeuda();
    }
    private void bloquearCliente(String cedula) {
        ClientePago clientePago = clientePagoRepositorio.buscarPorCedula(cedula);
        if (clientePago != null) {
            clientePago.setBloqueadoPorDeuda(true);
            clientePagoRepositorio.guardar(clientePago);
            log.warnf("Cliente %s bloqueado por deuda pendiente.", cedula);
        } else {
            log.errorf("No se pudo bloquear al cliente %s: no encontrado en ModuloPago.", cedula);
        }
    }
}
