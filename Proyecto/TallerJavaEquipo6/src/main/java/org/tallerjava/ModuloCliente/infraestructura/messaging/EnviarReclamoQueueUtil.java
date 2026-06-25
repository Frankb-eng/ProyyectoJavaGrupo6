package org.tallerjava.ModuloCliente.infraestructura.messaging;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.jboss.logging.Logger;


@ApplicationScoped
public class EnviarReclamoQueueUtil {

    private static final Logger log = Logger.getLogger(EnviarReclamoQueueUtil.class);

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:jboss/exported/jms/queue/reclamos")
    private Queue queueReclamos;

    public void enviarReclamo(ReclamoMessage reclamo) {
        String representacionJson = reclamo.toJson();
        log.infof("Publicando reclamo en la queue, idReclamo=%d", reclamo.idReclamo());
        jmsContext.createProducer().send(queueReclamos, representacionJson);
    }
}