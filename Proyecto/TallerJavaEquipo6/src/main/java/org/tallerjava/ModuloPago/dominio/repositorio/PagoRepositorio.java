package org.tallerjava.ModuloPago.dominio.repositorio;

import org.tallerjava.ModuloPago.dominio.Pago;

import java.time.LocalDate;
import java.util.List;

 public interface PagoRepositorio {

     void guardar(Pago pago);
     Pago buscarPorId(Long id);
     List<Pago> listarTodos();
     List<Pago> listarPagosPorCedulaYFechas(String cedula,LocalDate fechaIni,LocalDate fechaFin);
     Pago actualizar(Pago pago);
     void eliminar(Long id);
}
