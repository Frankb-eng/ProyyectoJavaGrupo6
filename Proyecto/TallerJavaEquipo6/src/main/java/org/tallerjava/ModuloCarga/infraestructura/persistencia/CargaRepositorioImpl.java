package org.tallerjava.ModuloCarga.infraestructura.persistencia;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.tallerjava.ModuloCarga.dominio.Carga;
import org.tallerjava.ModuloCarga.dominio.EstadoCarga;
import org.tallerjava.ModuloCarga.dominio.repositorio.CargaRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class CargaRepositorioImpl implements CargaRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    @Override
    public void save(Carga carga) {
        if (carga.getIdCarga() == 0) {
            em.persist(carga);
        } else {
            em.merge(carga);
        }
    }

    @Override
    public Optional<Carga> findById(long idCarga) {
        return Optional.ofNullable(em.find(Carga.class, idCarga));
    }

    @Override
    public Optional<Carga> findCargaActiva(String idCliente) {
        return em.createQuery(
                        "SELECT c FROM Carga c WHERE c.idCLiente = :id " +
                                "AND (c.estado = 'INICIADA' OR c.estado = 'EN_PROGRESO')",
                        Carga.class)
                .setParameter("id", idCliente)
                .getResultStream().findFirst();
    }

    @Override
    public Optional<Carga> findCargaActivaPorCargador(long idCargador) {
        return em.createQuery(
                        "SELECT c FROM Carga c WHERE c.idCargador = :id " +
                                "AND (c.estado = 'INICIADA' OR c.estado = 'EN_PROGRESO')",
                        Carga.class)
                .setParameter("id", idCargador)
                .getResultStream().findFirst();
    }

    @Override
    public List<Carga> findHistorico(String idCliente, LocalDate fechaIni, LocalDate fechaFin) {
        return em.createQuery(
                        "SELECT c FROM Carga c WHERE c.idCLiente = :id " +
                                "AND c.fecha >= :ini AND c.fecha <= :fin ORDER BY c.horaInicio DESC",
                        Carga.class)
                .setParameter("id", idCliente)
                .setParameter("ini", fechaIni)
                .setParameter("fin", fechaFin)
                .getResultList();
    }
}