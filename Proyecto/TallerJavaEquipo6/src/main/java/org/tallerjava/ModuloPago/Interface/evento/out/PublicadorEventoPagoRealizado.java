package org.tallerjava.ModuloPago.Interface.evento.out;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

public class PublicadorEventoPagoRealizado {

    private static final Logger log = Logger.getLogger(PublicadorEventoPagoRealizado.class);

    @Inject
    private Event<EventoPagoRealizado> pagoRealizadoEvento;

    @Inject
    private Event<EventoPagoRechazado> pagoRechazadoEvento;

    public void publicarPagoAceptado(){
        log.info("Publicando evento: PagoTarjetaAceptado");
        pagoRealizadoEvento.fire(new EventoPagoRealizado());
    }
    public void publicarPagoRechazado(){
        log.info("Publicando evento: PagoTarjetaRechazado");
        pagoRechazadoEvento.fire(new EventoPagoRechazado());
    }
}
