package org.tallerjava.ModuloMonitoreo.Interface.evento.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloPago.Interface.evento.out.EventoPagoRealizado;
import org.tallerjava.ModuloPago.Interface.evento.out.EventoPagoRechazado;
import org.tallerjava.ModuloMonitoreo.infraestructura.RegistradorDeMetricas;

@ApplicationScoped
public class ObserverModuloPago {

    private static final Logger log = Logger.getLogger(ObserverModuloPago.class);

    @Inject
    private RegistradorDeMetricas registradorDeMetricas;

    public void onPagoRealizado(@Observes EventoPagoRealizado evento) {
        if ("TARJETA".equals(evento.getTipoMedioPago())) {
            log.info("Evento recibido: PagoRealizado TARJETA — incrementando pagosTarjeta");
            registradorDeMetricas.pagosAprovadosTargeta();
        } else if ("UTE".equals(evento.getTipoMedioPago())) {
            log.info("Evento recibido: PagoRealizado UTE — incrementando pagosUTE");
            registradorDeMetricas.incrementarCounter(RegistradorDeMetricas.PAGOS_UTE);
        }
    }

    public void onPagoRechazado(@Observes EventoPagoRechazado evento) {
        log.info("Evento recibido: PagoRechazado — incrementando erroresTarjeta");
        registradorDeMetricas.pagosRechazadosTargeta();
    }
}