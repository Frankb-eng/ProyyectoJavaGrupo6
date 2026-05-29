package org.tallerjava.ModuloCarga.aplicacion;

import org.tallerjava.ModuloCarga.dominio.Carga;
import org.tallerjava.ModuloCarga.dominio.Cargador;
import org.tallerjava.ModuloCarga.dominio.EstacionCarga;

import java.time.LocalDate;
import java.util.List;


public interface ServicioCarga {
    long iniciarCarga(String cedulaCliente, long idCargador, long idMedioPago);
    Carga verCargaActual(String cedulaCliente);
    List<Carga> verHistorico(String cedulaCliente, LocalDate fechaIni, LocalDate fechaFin);
    void finalizarCarga(long idCargador, float consumoKwh, int minutosDemora);
    long altaEstacion(EstacionCarga estacion);
    long altaCargador(long idEstacion, Cargador cargador);
    List<EstacionCarga> obtenerEstaciones();
}