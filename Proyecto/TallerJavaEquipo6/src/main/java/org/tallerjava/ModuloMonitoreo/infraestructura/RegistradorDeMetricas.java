package org.tallerjava.ModuloMonitoreo.infraestructura;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class RegistradorDeMetricas {

    // nombres de las metricas (constantes para no escribir strings a mano)
    public static final String CARGAS_ACTIVAS    = "cargasActivas";
    public static final String CARGAS_REALIZADAS = "cargasRealizadas";
    public static final String PAGOS_TARJETA     = "pagosTarjeta";
    public static final String PAGOS_UTE         = "pagosUTE";
    public static final String ERRORES_TARJETA   = "erroresTarjeta";

    // referencia tipada para poder llamar a flush()
    private InfluxMeterRegistryPublicable meterRegistry;

    // todas las metricas como AtomicInteger para garantizar push consistente
    private final AtomicInteger cargasActivas    = new AtomicInteger(0);
    private final AtomicInteger cargasRealizadas = new AtomicInteger(0);
    private final AtomicInteger pagosTarjeta     = new AtomicInteger(0);
    private final AtomicInteger pagosUTE         = new AtomicInteger(0);
    private final AtomicInteger erroresTarjeta   = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        InfluxConfig config = new InfluxConfig() {
            @Override
            public String get(String s) {
                return null;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(10);
            }

            @Override
            public String db() {
                return "metricasTallerJava";
            }
        };

        this.meterRegistry = new InfluxMeterRegistryPublicable(config, Clock.SYSTEM);

        // todas las metricas registradas como Gauge con AtomicInteger
        // el Gauge lee el valor del AtomicInteger por referencia en cada push
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
    }

    // envia las metricas a InfluxDB de forma inmediata sin esperar el scheduler
    private void forzarPush() {
        try {
            meterRegistry.flush();
        } catch (Exception e) {
            // ignorar errores de conexion para no interrumpir el flujo de negocio
        }
    }

    // metodo generico — enruta al AtomicInteger correcto segun el nombre
    public void incrementarCounter(String nombreCounter) {
        if (CARGAS_REALIZADAS.equals(nombreCounter)) {
            cargasRealizadas.incrementAndGet();
        } else if (PAGOS_UTE.equals(nombreCounter)) {
            pagosUTE.incrementAndGet();
        }
        forzarPush();
    }

    public void incrementarCargasActivas() {
        cargasActivas.incrementAndGet();
        forzarPush();
    }

    public void decrementarCargasActivas() {
        cargasActivas.decrementAndGet();
        forzarPush();
    }

    public void pagosAprovadosTargeta() {
        pagosTarjeta.incrementAndGet();
        forzarPush();
    }

    public void pagosRechazadosTargeta() {
        erroresTarjeta.incrementAndGet();
        forzarPush();
    }

    // clase interna que extiende InfluxMeterRegistry para exponer publish()
    private static class InfluxMeterRegistryPublicable extends InfluxMeterRegistry {

        public InfluxMeterRegistryPublicable(InfluxConfig config, Clock clock) {
            super(config, clock);
        }

        public void flush() {
            publish();
        }
    }
}