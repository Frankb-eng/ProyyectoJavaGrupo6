package org.tallerjava.ModuloMonitoreo.infraestructura;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class RegistradorDeMetricas {

    public static final String CARGAS_ACTIVAS     = "cargasActivas";
    public static final String CARGAS_REALIZADAS  = "cargasRealizadas";
    public static final String PAGOS_TARJETA      = "pagosTarjeta";
    public static final String PAGOS_UTE          = "pagosUTE";
    public static final String ERRORES_TARJETA    = "erroresTarjeta";
    public static final String RECLAMOS_NEGATIVOS = "reclamosNegativos";

    private MeterRegistry meterRegistry;

    private final AtomicInteger cargasActivas     = new AtomicInteger(0);
    private final AtomicInteger cargasRealizadas  = new AtomicInteger(0);
    private final AtomicInteger pagosTarjeta      = new AtomicInteger(0);
    private final AtomicInteger pagosUTE          = new AtomicInteger(0);
    private final AtomicInteger erroresTarjeta    = new AtomicInteger(0);
    private final AtomicInteger reclamosNegativos = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        InfluxConfig config = new InfluxConfig() {
            @Override
            public String get(String s) { return null; }

            @Override
            public Duration step() { return Duration.ofSeconds(10); }

            @Override
            public String db() { return "metricasTallerJava"; }
        };

        this.meterRegistry = new InfluxMeterRegistry(config, Clock.SYSTEM);

        Gauge.builder(CARGAS_ACTIVAS, cargasActivas, AtomicInteger::get)
                .register(meterRegistry);
        Gauge.builder(CARGAS_REALIZADAS, cargasRealizadas, AtomicInteger::get)
                .register(meterRegistry);
        Gauge.builder(PAGOS_TARJETA, pagosTarjeta, AtomicInteger::get)
                .register(meterRegistry);
        Gauge.builder(PAGOS_UTE, pagosUTE, AtomicInteger::get)
                .register(meterRegistry);
        Gauge.builder(ERRORES_TARJETA, erroresTarjeta, AtomicInteger::get)
                .register(meterRegistry);
        Gauge.builder(RECLAMOS_NEGATIVOS, reclamosNegativos, AtomicInteger::get)
                .register(meterRegistry);
    }

    public void incrementarCounter(String nombreCounter) {
        if (CARGAS_REALIZADAS.equals(nombreCounter)) {
            cargasRealizadas.incrementAndGet();
        } else if (PAGOS_UTE.equals(nombreCounter)) {
            pagosUTE.incrementAndGet();
        } else if (RECLAMOS_NEGATIVOS.equals(nombreCounter)) {
            reclamosNegativos.incrementAndGet();
        }
    }

    public void incrementarCargasActivas() {
        cargasActivas.incrementAndGet();
    }

    public void decrementarCargasActivas() {
        cargasActivas.decrementAndGet();
    }

    public void pagosAprovadosTargeta() {
        pagosTarjeta.incrementAndGet();
    }

    public void pagosRechazadosTargeta() {
        erroresTarjeta.incrementAndGet();
    }
}