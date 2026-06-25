package org.tallerjava.ModuloCliente.Interface.evento.out;

import org.tallerjava.ModuloCliente.dominio.EstadoReclamo;

public class EventoReclamoClasificado {

    private final Long idReclamo;
    private final EstadoReclamo etiqueta;

    public EventoReclamoClasificado(Long idReclamo, EstadoReclamo etiqueta) {
        this.idReclamo = idReclamo;
        this.etiqueta = etiqueta;
    }

    public Long getIdReclamo() { return idReclamo; }
    public EstadoReclamo getEtiqueta() { return etiqueta; }
}