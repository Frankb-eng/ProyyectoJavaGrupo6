package org.tallerjava.ModuloCarga.Interface.remota.rest.dto;

import org.tallerjava.ModuloCarga.dominio.EstacionCarga;

public class EstacionDTO {

    private String descripcion;
    private String calle;
    private String departamento;
    private int longitud;
    private int latitud;

    public EstacionDTO() {}

    public EstacionCarga build() {
        return new EstacionCarga(descripcion, calle, departamento, longitud, latitud);
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public int getLongitud() { return longitud; }
    public void setLongitud(int longitud) { this.longitud = longitud; }
    public int getLatitud() { return latitud; }
    public void setLatitud(int latitud) { this.latitud = latitud; }
}