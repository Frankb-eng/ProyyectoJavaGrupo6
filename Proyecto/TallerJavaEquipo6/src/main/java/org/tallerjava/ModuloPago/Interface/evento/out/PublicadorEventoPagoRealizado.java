package org.tallerjava.ModuloPago.Interface.evento.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PublicadorEventoPagoRealizado {

    private static final Logger log = Logger.getLogger(PublicadorEventoPagoRealizado.class);

    @Inject
    private Event<EventoPagoRealizado> pagoRealizadoEvento;

    @Inject
    private Event<EventoPagoRechazado> pagoRechazadoEvento;

    public void publicarPagoAceptado(String tipoMedioPago) {
        log.infof("Publicando evento: PagoAceptado — tipo=%s", tipoMedioPago);
        pagoRealizadoEvento.fire(new EventoPagoRealizado(tipoMedioPago));
    }

    public void publicarPagoRechazado() {
        log.info("Publicando evento: PagoRechazado (Tarjeta)");
        pagoRechazadoEvento.fire(new EventoPagoRechazado());
    }
}