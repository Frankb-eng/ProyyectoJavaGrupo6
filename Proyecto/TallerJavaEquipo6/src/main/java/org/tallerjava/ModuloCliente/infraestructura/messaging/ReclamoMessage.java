package org.tallerjava.ModuloCliente.infraestructura.messaging;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;

import java.io.StringReader;
import java.io.StringWriter;


public record ReclamoMessage(
        Long idReclamo,
        String comentario,
        String cedulaCliente
) {

    public String toJson() {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("idReclamo", this.idReclamo)
                .add("comentario", this.comentario)
                .add("cedulaCliente", this.cedulaCliente)
                .build();

        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(sw);
        jsonWriter.write(jsonObject);
        jsonWriter.close();
        return sw.toString();
    }

    public static ReclamoMessage buildFromJson(String jsonReclamo) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonReclamo));
        JsonObject objeto = jsonReader.readObject();
        return new ReclamoMessage(
                objeto.getJsonNumber("idReclamo").longValue(),
                objeto.getString("comentario"),
                objeto.getString("cedulaCliente")
        );
    }
}