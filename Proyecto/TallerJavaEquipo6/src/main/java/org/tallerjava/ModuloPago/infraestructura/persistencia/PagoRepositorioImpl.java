package org.tallerjava.ModuloPago.infraestructura.persistencia;

import java.time.LocalDate;
import java.util.List;

import org.tallerjava.ModuloPago.dominio.Pago;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.tallerjava.ModuloPago.dominio.repositorio.PagoRepositorio;

@ApplicationScoped
public class PagoRepositorioImpl implements PagoRepositorio {

    @PersistenceContext(unitName = "tallerJavaPU")
    private EntityManager em;

    public void guardar(Pago pago) {
        em.persist(pago);
    }

    public Pago buscarPorId(Long id) {
        return em.find(Pago.class, id);
    }

    public List<Pago> listarTodos() {
        return em.createQuery(
                        "SELECT p FROM Pago p",
                        Pago.class)
                .getResultList();
    }

    public List<Pago> listarPagosPorCedulaYFechas(
            String cedula,
            LocalDate fechaIni,
            LocalDate fechaFin) {

        return em.createQuery(
                        "SELECT p " +
                                "FROM Pago p " +
                                "WHERE p.cedulaCliente = :cedula " +
                                "AND p.fecha BETWEEN :fechaIni AND :fechaFin",
                        Pago.class)
                .setParameter("cedula", cedula)
                .setParameter("fechaIni", fechaIni)
                .setParameter("fechaFin", fechaFin)
                .getResultList();
    }

    public Pago actualizar(Pago pago) {
        return em.merge(pago);
    }

    public void eliminar(Long id) {
        Pago pago = buscarPorId(id);

        if (pago != null) {
            em.remove(pago);
        }
    }
}