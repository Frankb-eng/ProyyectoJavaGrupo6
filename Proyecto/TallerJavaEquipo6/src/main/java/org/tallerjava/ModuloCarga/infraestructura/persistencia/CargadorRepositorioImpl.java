package org.tallerjava.ModuloCarga.infraestructura.persistencia;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCarga.dominio.Cargador;
import org.tallerjava.ModuloCarga.dominio.repositorio.CargadorRepositorio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class CargadorRepositorioImpl implements CargadorRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    @Override
    public void save(Cargador cargador) {
        if (cargador.getIdCargador() == 0) {
            em.persist(cargador);
        } else {
            em.merge(cargador);
        }
    }

    @Override
    public Optional<Cargador> findById(long idCargador) {
        return Optional.ofNullable(em.find(Cargador.class, idCargador));
    }
}