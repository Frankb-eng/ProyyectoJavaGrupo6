package org.tallerjava.ModuloCarga.dominio;
import jakarta.persistence.*;
@Entity
@Table(name = "estaciones_carga")
public class EstacionCarga {
    //atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEstacion;
    private String descripcion, calle, departamento;
    private int longitud, latitud;

    public EstacionCarga() {}
    public EstacionCarga(String descripcion, String calle, String departamento, int longitud, int latitud) {
        this.descripcion = descripcion;
        this.calle = calle;
        this.departamento = departamento;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public int getLatitud() {
        return latitud;
    }

    public void setLatitud(int latitud) {
        this.latitud = latitud;
    }

    public long getIdEstacion() {return idEstacion;}
    public void setIdEstacion(long idEstacion) {this.idEstacion = idEstacion;}
}
