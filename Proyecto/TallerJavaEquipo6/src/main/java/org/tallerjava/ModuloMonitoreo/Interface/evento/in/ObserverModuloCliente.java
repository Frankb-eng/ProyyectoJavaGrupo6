package org.tallerjava.ModuloMonitoreo.Interface.evento.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCliente.Interface.evento.out.EventoReclamoClasificado;
import org.tallerjava.ModuloCliente.dominio.EstadoReclamo;
import org.tallerjava.ModuloMonitoreo.infraestructura.RegistradorDeMetricas;

@ApplicationScoped
public class ObserverModuloCliente {

    private static final Logger log = Logger.getLogger(ObserverModuloCliente.class);

    @Inject
    private RegistradorDeMetricas registradorDeMetricas;

    public void onReclamoClasificado(@Observes EventoReclamoClasificado evento) {
        if (evento.getEtiqueta() == EstadoReclamo.NEGATIVO) {
            log.infof("Evento recibido: ReclamoClasificado NEGATIVO — incrementando reclamosNegativos");
            registradorDeMetricas.incrementarCounter(RegistradorDeMetricas.RECLAMOS_NEGATIVOS);
        }
    }
}