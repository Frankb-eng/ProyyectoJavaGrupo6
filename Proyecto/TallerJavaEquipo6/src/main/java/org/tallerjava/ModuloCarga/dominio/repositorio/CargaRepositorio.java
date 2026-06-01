package org.tallerjava.ModuloCarga.dominio.repositorio;

import org.tallerjava.ModuloCarga.dominio.Carga;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CargaRepositorio {

    void save(Carga carga);
    Optional<Carga> findCargaActiva(String idCliente);
    Optional<Carga> findById(long idCarga);
    List<Carga> findHistorico(String idCliente, LocalDate fechaIni, LocalDate fechaFin);
    Optional<Carga> findCargaActivaPorCargador(long idCargador);
}