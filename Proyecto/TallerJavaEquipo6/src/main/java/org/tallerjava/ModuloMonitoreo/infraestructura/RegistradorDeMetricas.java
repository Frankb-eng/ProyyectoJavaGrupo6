package org.tallerjava.ModuloMonitoreo.infraestructura;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class RegistradorDeMetricas {

    // nombres de los contadores (constantes para no escribir strings a mano)
    
    public static final String CARGAS_ACTIVAS  = "cargasActivas";
    public static final String CARGAS_REALIZADAS = "cargasRealizadas";
    public static final String PAGOS_TARJETA     = "pagosTarjeta";
    public static final String PAGOS_UTE         = "pagosUTE";
    public static final String ERRORES_TARJETA   = "erroresTarjeta";

    private MeterRegistry meterRegistry;
    // una sola instancia compartida — se crea una vez y se reutiliza siempre

    private final AtomicInteger cargasActivas = new AtomicInteger(0);
    private final AtomicInteger pagosTarjeta = new AtomicInteger(0);
    private final AtomicInteger erroresTarjeta = new AtomicInteger(0);

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

        Gauge.builder(CARGAS_ACTIVAS, cargasActivas, AtomicInteger::get)
         .register(meterRegistry);

        Gauge.builder(PAGOS_TARJETA, pagosTarjeta, AtomicInteger::get).register(meterRegistry);
        Gauge.builder(ERRORES_TARJETA, erroresTarjeta, AtomicInteger::get).register(meterRegistry);
    }








    public void incrementarCounter(String nombreCounter) {
        meterRegistry.counter(nombreCounter).increment();
        // le suma 1 al contador con ese nombre en InfluxDB
    }

    public void incrementarCargasActivas() {
        cargasActivas.incrementAndGet();
        // +1 cuando se inicia una carga
    }

    public void decrementarCargasActivas() {
        cargasActivas.decrementAndGet();
        // -1 cuando se finaliza una carga
}
    public void pagosAprovadosTargeta() {
        pagosTarjeta.incrementAndGet();
        // +1 cuando se realiza un pago con targeta
    }

    public void pagosRechazadosTargeta() {
        erroresTarjeta.incrementAndGet();
        // +1 cuando se rechaza un pago con targeta
    }

    /*public void decrementarCounter(String nombreCounter) {
        meterRegistry.counter(nombreCounter).increment(-1);
        // le resta 1 — necesario para cargas activas cuando finaliza una carga
    }*/
}