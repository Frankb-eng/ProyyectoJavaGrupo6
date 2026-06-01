package org.tallerjava.ModuloCarga.infraestructura.persistencia;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCarga.dominio.EstacionCarga;
import org.tallerjava.ModuloCarga.dominio.repositorio.EstacionRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class EstacionRepositorioImpl implements EstacionRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    @Override
    public void save(EstacionCarga estacion) {
        if (estacion.getIdEstacion() == 0) {
            em.persist(estacion);
        } else {
            em.merge(estacion);
        }
    }

    @Override
    public Optional<EstacionCarga> findById(long idEstacion) {
        return Optional.ofNullable(em.find(EstacionCarga.class, idEstacion));
    }

    @Override
    public List<EstacionCarga> findAll() {
        return em.createQuery("SELECT e FROM EstacionCarga e", EstacionCarga.class)
                .getResultList();
    }
}