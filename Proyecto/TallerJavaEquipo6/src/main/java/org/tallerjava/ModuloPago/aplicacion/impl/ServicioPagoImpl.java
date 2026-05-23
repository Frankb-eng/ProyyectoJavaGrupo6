package org.tallerjava.ModuloPago.aplicacion.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.repositorio.ClienteRepositorio;
import org.tallerjava.ModuloPago.Interface.local.InterfaceLocalPago;
import org.tallerjava.ModuloPago.Interface.remota.rest.dto.PagoDTO;
import org.tallerjava.ModuloPago.aplicacion.ServicioPago;
import org.tallerjava.ModuloPago.dominio.Pago;
import org.tallerjava.ModuloPago.dominio.repositorio.PagoRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private static final Logger log = Logger.getLogger(ServicioPagoImpl.class);

    @Override
    public void altaPago(String cedula, Pago pago) {
        pagoRepositorio.guardar(pago);
        Cliente c = clienteRepositorio.buscarPorCedula(cedula);
        c.agregarPago(pago);
        clienteRepositorio.actualizar(c);
    }
    @Override
    public boolean pagarCarga(String cedulaCliente, long importeCentavos, long idMedioPago) {
        // TODO: implementar logica de cobro — pendiente compañero
        log.infof("pagarCarga invocado — cliente=%s, importe=%d centavos, medioPago=%d",
                cedulaCliente, importeCentavos, idMedioPago);
        return true;
    }

    public List<Pago> consultarPagos(String cedula, LocalDate fechaIni, LocalDate fechaFin){ return pagoRepositorio.listarPagosPorCedulaYFechas(cedula,fechaIni,fechaFin); }

    public boolean pagarCarga(String cedula, int importe, Long idMedioPago){
        Pago pago = new Pago(cedula,importe,idMedioPago);
        this.altaPago(cedula, pago);
        return true;
    }
}
