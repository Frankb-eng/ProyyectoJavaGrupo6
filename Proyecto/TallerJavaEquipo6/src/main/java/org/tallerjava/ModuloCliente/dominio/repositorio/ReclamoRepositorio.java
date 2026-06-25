package org.tallerjava.ModuloCliente.dominio.repositorio;

import org.tallerjava.ModuloCliente.dominio.Reclamo;

public interface ReclamoRepositorio {

    Reclamo buscarPorId(Long id);

    void actualizar(Reclamo reclamo);
}