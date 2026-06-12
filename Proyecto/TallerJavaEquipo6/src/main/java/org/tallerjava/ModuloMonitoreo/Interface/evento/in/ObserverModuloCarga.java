package org.tallerjava.ModuloMonitoreo.Interface.evento.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCarga.Interface.evento.out.EventoCargaFinalizada;
import org.tallerjava.ModuloCarga.Interface.evento.out.EventoCargaIniciada;
import org.tallerjava.ModuloMonitoreo.infraestructura.RegistradorDeMetricas;

@ApplicationScoped
public class ObserverModuloCarga {

    private static final Logger log = Logger.getLogger(ObserverModuloCarga.class);
    // Logger para verificar en la consola que los eventos estan siendo recibidos

    

    @Inject
    private RegistradorDeMetricas registradorDeMetricas;
    // inyectamos el registrador — es quien sabe como hablar con InfluxDB

    public void onCargaIniciada(@Observes EventoCargaIniciada evento) {
        
        log.info("Evento recibido: CargaIniciada — incrementando cargasActivas");
        registradorDeMetricas.incrementarCargasActivas();
        //registradorDeMetricas.incrementarCounter(RegistradorDeMetricas.CARGAS_INICIADAS);
        // +1 en cargas activas — hay una carga mas en curso ahora mismo
    }

    public void onCargaFinalizada(@Observes EventoCargaFinalizada evento) {
        log.info("Evento recibido: CargaFinalizada — actualizando metricas");
        //registradorDeMetricas.incrementarCounter(RegistradorDeMetricas.CARGAS_INICIADAS);
        // -1 en cargas activas — esa carga ya no esta en curso

        registradorDeMetricas.decrementarCargasActivas();
        // -1 en cargas activas — esa carga ya no esta en curso

        
        registradorDeMetricas.incrementarCounter(RegistradorDeMetricas.CARGAS_REALIZADAS);
        // +1 en cargas realizadas — contador historico que solo sube
    }
}