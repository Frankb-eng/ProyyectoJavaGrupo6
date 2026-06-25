package org.tallerjava.ModuloCliente.infraestructura.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCliente.aplicacion.ServicioClientes;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "queue/reclamos"),
                @ActivationConfigProperty(
                        propertyName = "maxSession",
                        propertyValue = "1")
        }
)
public class ClasificadorReclamoConsumer implements MessageListener {

    private static final Logger log = Logger.getLogger(ClasificadorReclamoConsumer.class);

    @Inject
    private ServicioClientes servicioClientes;

    public ClasificadorReclamoConsumer() {}

    @Override
    public void onMessage(Message message) {
        try {
            String body = message.getBody(String.class);
            log.infof("Reclamo recibido desde la queue: %s", body);

            ReclamoMessage reclamoMessage = ReclamoMessage.buildFromJson(body);
            servicioClientes.clasificarReclamo(reclamoMessage.idReclamo());

            log.infof("Reclamo idReclamo=%d procesado", reclamoMessage.idReclamo());

        } catch (JMSException e) {
            log.errorf("Error al leer el mensaje de reclamo: %s", e.getMessage());
        }
    }
}