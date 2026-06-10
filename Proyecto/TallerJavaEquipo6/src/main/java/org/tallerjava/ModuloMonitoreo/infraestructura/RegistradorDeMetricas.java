package org.tallerjava.ModuloMonitoreo.infraestructura;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import io.micrometer.core.instrument.Clock;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;

@ApplicationScoped
public class RegistradorDeMetricas {

    // nombres de los contadores (constantes para no escribir strings a mano)
    public static final String CARGAS_ACTIVAS    = "cargasActivas";
    public static final String CARGAS_REALIZADAS = "cargasRealizadas";
    public static final String PAGOS_TARJETA     = "pagosTarjeta";
    public static final String PAGOS_UTE         = "pagosUTE";
    public static final String ERRORES_TARJETA   = "erroresTarjeta";

    private MeterRegistry meterRegistry;
    // una sola instancia compartida — se crea una vez y se reutiliza siempre

    @PostConstruct
    public void init() {
        // @PostConstruct se ejecuta automticamente justo despues de que
        // jakarta crea este bean y antes de que alguien lo use
        InfluxConfig config = new InfluxConfig() {
            @Override
            public String get(String s) {
                return null; 
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(10);
                // cada 10 segundos envia las metricas acumuladas a InfluxDB
            }

            @Override
            public String db() {
                return "metricasTallerJava";
                // nombre de la base de datos en InfluxDB donde se guardan las metricas
            }
        };

        this.meterRegistry = new InfluxMeterRegistry(config, Clock.SYSTEM);
        // se crea UNA sola vez cuando arranca la app y se reutiliza en cada llamada
    }

    public void incrementarCounter(String nombreCounter) {
        meterRegistry.counter(nombreCounter).increment();
        // le suma 1 al contador con ese nombre en InfluxDB
    }

    public void decrementarCounter(String nombreCounter) {
        meterRegistry.counter(nombreCounter).increment(-1);
        // le resta 1 — necesario para cargas activas cuando finaliza una carga
    }
}