package org.tallerjava.ModuloCarga.infraestructura.persistencia;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCarga.dominio.ClienteCarga;
import org.tallerjava.ModuloCarga.dominio.repositorio.ClienteCargaRepositorio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ClienteCargaRepositorioImpl implements ClienteCargaRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    @Override
    public void guardar(ClienteCarga cliente) {
        em.merge(cliente);
    }

    @Override
    public Optional<ClienteCarga> findById(String cedula) {
        return Optional.ofNullable(em.find(ClienteCarga.class, cedula));
    }
}