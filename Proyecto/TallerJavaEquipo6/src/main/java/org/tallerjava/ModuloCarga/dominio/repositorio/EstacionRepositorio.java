package org.tallerjava.ModuloCarga.dominio.repositorio;

import org.tallerjava.ModuloCarga.dominio.EstacionCarga;

import java.util.List;
import java.util.Optional;


public interface EstacionRepositorio {

    void save(EstacionCarga estacion);
    Optional<EstacionCarga> findById(long idEstacion);
    List<EstacionCarga> findAll();
}