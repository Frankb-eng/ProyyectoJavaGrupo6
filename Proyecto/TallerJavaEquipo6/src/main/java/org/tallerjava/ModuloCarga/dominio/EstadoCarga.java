package org.tallerjava.ModuloCarga.dominio;

public enum EstadoCarga {

    /** inicia la sesion pero todavía no hay carga de corriente */
    INICIADA,

    /** ahora cargando corriente  */
    EN_PROGRESO,

    /** ya finalizado se cerro la sesion, el cable desenchufado y ya se disparo evento de cobro */
    COMPLETADA,

    /** cancelado antes de completarse la sesion */
    CANCELADA
}
