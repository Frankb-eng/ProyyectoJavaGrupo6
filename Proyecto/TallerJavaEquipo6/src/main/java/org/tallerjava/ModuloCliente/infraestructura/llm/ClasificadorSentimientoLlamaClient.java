package org.tallerjava.ModuloCliente.infraestructura.llm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.tallerjava.ModuloCliente.dominio.EstadoReclamo;

import java.io.StringReader;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ClasificadorSentimientoLlamaClient {

    private static final Logger log = Logger.getLogger(ClasificadorSentimientoLlamaClient.class);

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODELO = "llama2";
    // puede demorar varios minutos; se da un margen amplio antes de considerarla fallida
    private static final int TIMEOUT_MINUTOS = 5;
    public EstadoReclamo clasificar(String comentario) {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(TIMEOUT_MINUTOS, TimeUnit.MINUTES)
                .readTimeout(TIMEOUT_MINUTOS, TimeUnit.MINUTES)
                .build();

        try {
            String prompt = construirPrompt(comentario);

            JsonObject requestBody = Json.createObjectBuilder()
                    .add("model", MODELO)
                    .add("prompt", prompt)
                    .add("stream", false)
                    .build();

            Response response = client.target(OLLAMA_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody.toString(), MediaType.APPLICATION_JSON));

            if (response.getStatus() != 200) {
                log.warnf("Ollama respondio status %d, se marca el reclamo como PENDIENTE", response.getStatus());
                return EstadoReclamo.PENDIENTE;
            }

            String body = response.readEntity(String.class);
            return interpretarRespuesta(body);

        } catch (Exception e) {
            // cubre timeout, conexion rechazada, etc.
            log.warnf("No se pudo clasificar el reclamo via LLM, se marca PENDIENTE: %s", e.getMessage());
            return EstadoReclamo.PENDIENTE;
        } finally {
            client.close();
        }
    }

    private String construirPrompt(String comentario) {
        return "Clasifica el sentimiento del siguiente reclamo de un cliente de un servicio "
                + "de carga de vehiculos electricos. Respondé unicamente con una de estas "
                + "tres palabras, sin explicacion adicional: POSITIVO, NEGATIVO o NEUTRO.\n\n"
                + "Reclamo: \"" + comentario + "\"\n\n"
                + "Clasificacion:";
    }

    private EstadoReclamo interpretarRespuesta(String responseBody) {
        JsonObject json = Json.createReader(new StringReader(responseBody)).readObject();
        String texto = json.getString("response", "").toUpperCase(Locale.ROOT);

        if (texto.contains("NEGATIVO")) {
            return EstadoReclamo.NEGATIVO;
        } else if (texto.contains("POSITIVO")) {
            return EstadoReclamo.POSITIVO;
        } else if (texto.contains("NEUTRO")) {
            return EstadoReclamo.NEUTRO;
        }

        log.warnf("Respuesta del LLM no reconocida, se marca PENDIENTE. Respuesta: %s", texto);
        return EstadoReclamo.PENDIENTE;
    }
}