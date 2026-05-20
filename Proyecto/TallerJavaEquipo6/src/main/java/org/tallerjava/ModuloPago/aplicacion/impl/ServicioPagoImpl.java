package org.tallerjava.ModuloPago.aplicacion.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCliente.dominio.Cliente;
import org.tallerjava.ModuloCliente.dominio.repositorio.ClienteRepositorio;
import org.tallerjava.ModuloPago.Interface.remota.rest.dto.PagoDTO;
import org.tallerjava.ModuloPago.aplicacion.ServicioPago;
import org.tallerjava.ModuloPago.dominio.Pago;
import org.tallerjava.ModuloPago.dominio.repositorio.PagoRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class ServicioPagoImpl implements ServicioPago
{
    @Inject
    PagoRepositorio pagoRepositorio;

    @Inject
    ClienteRepositorio clienteRepositorio;

    @Override
    public void altaPago(String cedula, Pago pago) {
        pagoRepositorio.guardar(pago);
        Cliente c = clienteRepositorio.buscarPorCedula(cedula);
        c.agregarPago(pago);
        clienteRepositorio.actualizar(c);
    }

    public List<Pago> consultarPagos(String cedula, LocalDate fechaIni, LocalDate fechaFin){ return pagoRepositorio.listarPagosPorCedulaYFechas(cedula,fechaIni,fechaFin); }
}
