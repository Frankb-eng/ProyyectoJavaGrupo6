package org.tallerjava.ModuloMonitoreo.Interface.evento.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloMonitoreo.infraestructura.RegistradorDeMetricas;

@ApplicationScoped
public class ObserverModuloPago {

    private static final Logger log = Logger.getLogger(ObserverModuloCarga.class);
    // Logger para verificar en la consola que los eventos estan siendo recibidos



    @Inject
    private RegistradorDeMetricas registradorDeMetricas;
    
}
