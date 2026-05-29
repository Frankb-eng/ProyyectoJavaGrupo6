package org.tallerjava.ModuloCarga.dominio.repositorio;

import org.tallerjava.ModuloCarga.dominio.Cargador;

import java.util.Optional;


public interface CargadorRepositorio {

    void save(Cargador cargador);
    Optional<Cargador> findById(long idCargador);
}