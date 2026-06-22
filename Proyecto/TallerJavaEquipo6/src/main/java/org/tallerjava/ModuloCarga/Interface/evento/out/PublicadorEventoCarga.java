package org.tallerjava.ModuloCarga.Interface.evento.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PublicadorEventoCarga {

    private static final Logger log = Logger.getLogger(PublicadorEventoCarga.class);
    // para registrar en la consola de WildFly cuando se publican eventos
    //y para verificar que los eventos se estan disparando correctamente

    @Inject
    private Event<EventoCargaIniciada> cargaIniciadaEvento;
   

    @Inject
    private Event<EventoCargaFinalizada> cargaFinalizadaEvento;
    
    public void publicarCargaIniciada() {
        log.info("Publicando evento: CargaIniciada");
        cargaIniciadaEvento.fire(new EventoCargaIniciada());
    }

    public void publicarCargaFinalizada() {
        log.info("Publicando evento: CargaFinalizada");
        cargaFinalizadaEvento.fire(new EventoCargaFinalizada());
        
    }
}