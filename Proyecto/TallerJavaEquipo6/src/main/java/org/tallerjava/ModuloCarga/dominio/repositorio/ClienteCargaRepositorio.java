package org.tallerjava.ModuloCarga.dominio.repositorio;

import org.tallerjava.ModuloCarga.dominio.ClienteCarga;

import java.util.Optional;

public interface ClienteCargaRepositorio {

    void guardar(ClienteCarga cliente);
    Optional<ClienteCarga> findById(String idCliente);
}
