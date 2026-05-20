package org.tallerjava.ModuloCliente.dominio;

import jakarta.persistence.*;
import org.tallerjava.ModuloPago.dominio.Pago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {

    @Id
    private String cedula;

    private String nombreCompleto;
    private String telefono;
    private String contrasena;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cedula_cliente")
    private List<MedioPago> mediosDePago = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cedula_cliente")
    private List<Reclamo> reclamos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cedula_cliente")
    private List<Pago> pagos = new ArrayList<>();

    protected Cliente() {}

    protected Cliente(String cedula, String nombreCompleto, String telefono, String contrasena) {
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    public void agregarMedioPago(MedioPago medioPago) {
        if (mediosDePago.isEmpty()) {
            medioPago.setPredeterminado(true);
        }
        mediosDePago.add(medioPago);
    }

    public MedioPago obtenerMedioPagoPredeterminado() {

        for(MedioPago mp: mediosDePago){
            if(mp.esPredeterminado()){
                return mp;
            }
        }
        throw new IllegalStateException(
                "El cliente " + cedula + " no tiene medio de pago predeterminado");

    }

    public abstract double aplicarDescuento(double importe);

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public List<MedioPago> getMediosDePago() {
        return Collections.unmodifiableList(mediosDePago);
        //se retorna asi para evitar que desde afuera se modifique la lista de medios de pago directamente
    }

    public void agregarReclamo(Reclamo reclamo) {
    reclamos.add(reclamo);
}

    public List<Reclamo> getReclamos() {
        return Collections.unmodifiableList(reclamos);
        //se retorna asi para evitar que desde afuera se modifique la lista de reclamos directamente
    }

    public void agregarPago(Pago pago){ pagos.add(pago); }
    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
        //se retorna asi para evitar que desde afuera se modifique la lista de reclamos directamente
    }
}
