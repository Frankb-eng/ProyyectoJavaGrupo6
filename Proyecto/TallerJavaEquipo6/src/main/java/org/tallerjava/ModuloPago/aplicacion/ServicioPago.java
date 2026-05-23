package org.tallerjava.ModuloPago.aplicacion;

import jakarta.ws.rs.core.Response;
import org.tallerjava.ModuloPago.dominio.Pago;

import java.time.LocalDate;
import java.util.List;

public interface ServicioPago {

    void altaPago(String cedula, Pago pago);

    List<Pago> consultarPagos(String cedula, LocalDate fechaIni, LocalDate fechaFin);

}
