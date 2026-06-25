package org.tallerjava.ModuloCliente.Interface.evento.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCliente.dominio.EstadoReclamo;

@ApplicationScoped
public class PublicadorEventoReclamo {

    private static final Logger log = Logger.getLogger(PublicadorEventoReclamo.class);

    @Inject
    private Event<EventoReclamoClasificado> reclamoClasificadoEvento;

    public void publicarReclamoClasificado(Long idReclamo, EstadoReclamo etiqueta) {
        log.infof("Publicando evento: ReclamoClasificado — idReclamo=%d, etiqueta=%s", idReclamo, etiqueta);
        reclamoClasificadoEvento.fire(new EventoReclamoClasificado(idReclamo, etiqueta));
    }
}