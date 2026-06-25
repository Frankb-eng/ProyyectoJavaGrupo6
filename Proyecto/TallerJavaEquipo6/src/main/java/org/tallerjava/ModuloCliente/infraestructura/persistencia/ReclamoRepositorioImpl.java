package org.tallerjava.ModuloCliente.infraestructura.persistencia;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCliente.dominio.Reclamo;
import org.tallerjava.ModuloCliente.dominio.repositorio.ReclamoRepositorio;

@ApplicationScoped
public class ReclamoRepositorioImpl implements ReclamoRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Reclamo buscarPorId(Long id) {
        return em.find(Reclamo.class, id);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void actualizar(Reclamo reclamo) {
        em.merge(reclamo);
    }
}