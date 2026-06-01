package org.tallerjava.ModuloCarga.Interface.remota.rest.dto;

import org.tallerjava.ModuloCarga.dominio.Cargador;
import org.tallerjava.ModuloCarga.dominio.EstadoCargador;
import org.tallerjava.ModuloCarga.dominio.TipoCargador;
import org.tallerjava.ModuloCarga.dominio.TipoConector;

public class CargadorDTO {

    private long idEstacion;
    private String tipo;
    private boolean tieneCable;
    private String tipoConector;
    private int potenciaMinima;

    public CargadorDTO() {}

    public Cargador build() {
        return new Cargador(
                TipoCargador.valueOf(tipo.toUpperCase()),
                tieneCable,
                TipoConector.valueOf(tipoConector.toUpperCase()),
                EstadoCargador.DISPONIBLE,
                null,
                null,
                potenciaMinima
        );
    }

    public long getIdEstacion() { return idEstacion; }
    public void setIdEstacion(long idEstacion) { this.idEstacion = idEstacion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isTieneCable() { return tieneCable; }
    public void setTieneCable(boolean tieneCable) { this.tieneCable = tieneCable; }
    public String getTipoConector() { return tipoConector; }
    public void setTipoConector(String tipoConector) { this.tipoConector = tipoConector; }
    public int getPotenciaMinima() { return potenciaMinima; }
    public void setPotenciaMinima(int potenciaMinima) { this.potenciaMinima = potenciaMinima; }
}
