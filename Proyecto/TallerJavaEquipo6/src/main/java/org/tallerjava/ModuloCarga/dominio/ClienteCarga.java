package org.tallerjava.ModuloCarga.dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "cargas_cliente")
public class ClienteCarga {

    @Id
    private String cedula;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo;   // COMUN o PROFESIONAL

    public ClienteCarga() {}

    public ClienteCarga(String cedula, String nombre, String tipo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
