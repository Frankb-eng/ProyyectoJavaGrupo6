package org.tallerjava.ModuloPago.dominio;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int importe;
    private LocalDate fecha;
    private LocalDateTime hora;
    @Column(name = "cedula_cliente")
    private String cedulaCliente;
    private Long idMedioPago;
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    public Pago() {} //porque JPA lo quiere

    public Pago(String cliente, int importe, Long idMedioPago ) {
        this.importe = importe;
        this.fecha = LocalDate.now();
        this.hora = LocalDateTime.now();
        this.idMedioPago = idMedioPago;
        this.cedulaCliente = cliente;
        this.estado = EstadoPago.PROSESANDO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getImporte() { return importe; }
    public void setImporte(int importe) { this.importe = importe; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalDateTime getHora() { return hora; }
    public void setHora(LocalDateTime hora) { this.hora = hora; }
    public Long getIdMedioPago() { return idMedioPago; }
    public void setIdMedioPago(Long idMedioPago) { this.idMedioPago = idMedioPago; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }
}
