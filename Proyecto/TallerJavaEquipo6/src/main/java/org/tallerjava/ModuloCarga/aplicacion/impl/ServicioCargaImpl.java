package org.tallerjava.ModuloCarga.aplicacion.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCarga.aplicacion.ServicioCarga;
import org.tallerjava.ModuloCarga.dominio.Carga;
import org.tallerjava.ModuloCarga.dominio.Cargador;
import org.tallerjava.ModuloCarga.dominio.EstacionCarga;
import org.tallerjava.ModuloCarga.dominio.EstadoCarga;
import org.tallerjava.ModuloCarga.dominio.EstadoCargador;
import org.tallerjava.ModuloCarga.dominio.repositorio.CargaRepositorio;
import org.tallerjava.ModuloCarga.dominio.repositorio.CargadorRepositorio;
import org.tallerjava.ModuloCarga.dominio.repositorio.ClienteCargaRepositorio;
import org.tallerjava.ModuloCarga.dominio.repositorio.EstacionRepositorio;
import org.tallerjava.ModuloPago.Interface.local.InterfaceLocalPago;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class ServicioCargaImpl implements ServicioCarga {

    private static final Logger log = Logger.getLogger(ServicioCargaImpl.class);

    private static final float TARIFA_KWH               = 5.0f;
    private static final float TARIFA_DEMORA_POR_MINUTO = 2.0f;

    @Inject
    private CargaRepositorio repoCarga;

    @Inject
    private CargadorRepositorio repoCargador;

    @Inject
    private EstacionRepositorio repoEstacion;

    @Inject
    private ClienteCargaRepositorio repoCliente;

    @Inject
    InterfaceLocalPago interfaceLocalPago;


    @Override
    @Transactional
    public long iniciarCarga(String cedulaCliente, long idCargador, long idMedioPago) {
        repoCliente.findById(cedulaCliente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado en ModuloCarga: " + cedulaCliente));

        Optional<Carga> cargaActiva = repoCarga.findCargaActiva(cedulaCliente);
        if (cargaActiva.isPresent()) {
            throw new IllegalStateException(
                    "El cliente " + cedulaCliente + " ya tiene una carga activa");
        }

        Cargador cargador = repoCargador.findById(idCargador)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cargador no encontrado: " + idCargador));

        if (cargador.getEstado() != EstadoCargador.DISPONIBLE) {
            throw new IllegalStateException(
                    "El cargador " + idCargador + " no está disponible");
        }

        Carga carga = new Carga(
                LocalDate.now(), LocalDateTime.now(), null, null,
                0, 0, cedulaCliente, 0, EstadoCarga.INICIADA
        );
        carga.setIdCargador(idCargador);
        carga.setIdMedioPago(idMedioPago);

        cargador.setEstado(EstadoCargador.OCUPADO);
        cargador.setTiempoEstimadoFinalizacion(LocalDateTime.now().plusHours(1));

        repoCargador.save(cargador);
        repoCarga.save(carga);

        return carga.getIdCarga();
    }


    @Override
    public Carga verCargaActual(String cedulaCliente) {
        return repoCarga.findCargaActiva(cedulaCliente).orElse(null);
    }


    @Override
    public List<Carga> verHistorico(String cedulaCliente, LocalDate fechaIni, LocalDate fechaFin) {
        return repoCarga.findHistorico(cedulaCliente, fechaIni, fechaFin);
    }


    @Override
    @Transactional
    public void finalizarCarga(long idCargador, float consumoKwh, int minutosDemora) {
        log.infof("Finalizando carga: cargador=%d, consumo=%.2f kWh, demora=%d min",
                idCargador, consumoKwh, minutosDemora);


        Cargador cargador = repoCargador.findById(idCargador)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cargador no encontrado: " + idCargador));

        Carga carga = repoCarga.findCargaActivaPorCargador(idCargador)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe carga activa en el cargador: " + idCargador));

        float importeEnergia = consumoKwh * TARIFA_KWH;
        float importeDemora  = minutosDemora * TARIFA_DEMORA_POR_MINUTO;

        log.infof("Importes: energía=%.2f, demora=%.2f", importeEnergia, importeDemora);

        carga.setHoraFin(LocalDateTime.now());
        carga.setImporteTotal(importeEnergia);
        carga.setRecargoPorDemora(importeDemora);
        carga.setEstado(EstadoCarga.COMPLETADA);

        cargador.setEstado(EstadoCargador.DISPONIBLE);
        cargador.setTiempoEstimadoFinalizacion(null);

        repoCarga.save(carga);
        repoCargador.save(cargador);

        int importeTotalCentavos = Math.round((importeEnergia + importeDemora) * 100);

        boolean pagoExitoso = interfaceLocalPago.pagarCarga(
                carga.getIdCLiente(),
                importeTotalCentavos,
                carga.getIdMedioPago()
        );

        if (!pagoExitoso) {
            throw new IllegalStateException(
                    "El pago fallo para el cliente: " + carga.getIdCLiente());
        }

        log.infof("Carga finalizada y pago procesado: idCarga=%d", carga.getIdCarga());
    }


    @Override
    @Transactional
    public long altaEstacion(EstacionCarga estacion) {
        log.infof("Alta de estación: %s", estacion.getDescripcion());
        repoEstacion.save(estacion);
        return estacion.getIdEstacion();
    }


    @Override
    @Transactional
    public long altaCargador(long idEstacion, Cargador cargador) {
        log.infof("Alta de cargador en estación: %d", idEstacion);
        repoEstacion.findById(idEstacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estación no encontrada: " + idEstacion));
        repoCargador.save(cargador);
        return cargador.getIdCargador();
    }


    @Override
    public List<EstacionCarga> obtenerEstaciones() {
        return repoEstacion.findAll();
    }
}
