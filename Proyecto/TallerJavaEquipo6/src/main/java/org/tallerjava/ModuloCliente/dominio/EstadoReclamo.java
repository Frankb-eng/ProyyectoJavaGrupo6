package org.tallerjava.ModuloCliente.dominio;

public enum EstadoReclamo {

    /** todavia no fue clasificado o la clasificacion fallo y espera reintento */
    PENDIENTE,

    /** ollama clasifico el comentario como positivo */
    POSITIVO,

    /** ollama clasifico el comentario como negativo */
    NEGATIVO,

    /** el comentario como neutr0 */
    NEUTRO
}