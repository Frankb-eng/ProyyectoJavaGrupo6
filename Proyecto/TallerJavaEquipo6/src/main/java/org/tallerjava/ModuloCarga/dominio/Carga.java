package org.tallerjava.ModuloCarga.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "cargas")
public class Carga {
    //atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCarga;
    private LocalDate fecha;
    private LocalDateTime horaInicio, horaFin, horaEstimadaFin;  //horaEstimadaFin: solo si estado=activa
    private float importeTotal, recargoPorDemora;
    @Column(name = "cedula_cliente")
    private String idCLiente;
    private int porcentajeAvance;
    //solo si estado=activa
    @Enumerated(EnumType.STRING)
    private EstadoCarga estado;
    private long idMedioPago;
    private long idCargador;


    public Carga() {}
    public Carga(LocalDate fecha, LocalDateTime horaInicio, LocalDateTime horaFin, LocalDateTime horaEstimadaFin, float importeTotal, float recargoPorDemora, String idCLiente, int porcentajeAvance, EstadoCarga estado) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horaEstimadaFin = horaEstimadaFin;
        this.importeTotal = importeTotal;
        this.recargoPorDemora = recargoPorDemora;
        this.idCLiente = idCLiente;
        this.porcentajeAvance = porcentajeAvance;
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public LocalDateTime getHoraEstimadaFin() {
        return horaEstimadaFin;
    }

    public void setHoraEstimadaFin(LocalDateTime horaEstimadaFin) {
        this.horaEstimadaFin = horaEstimadaFin;
    }

    public float getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(float importeTotal) {
        this.importeTotal = importeTotal;
    }

    public float getRecargoPorDemora() {
        return recargoPorDemora;
    }

    public void setRecargoPorDemora(float recargoPorDemora) {
        this.recargoPorDemora = recargoPorDemora;
    }

    public String getIdCLiente() {
        return idCLiente;
    }
    public void setIdCLiente(String idCLiente) {
        this.idCLiente = idCLiente;
    }

    public int getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(int porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public EstadoCarga getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarga estado) {
        this.estado = estado;
    }

    public long getIdCarga()      { return idCarga; }
    public void setIdCarga(long idCarga) {this.idCarga = idCarga;}
    public long getIdMedioPago()  { return idMedioPago; }
    public void setIdMedioPago(long idMedioPago) {this.idMedioPago = idMedioPago;}
    public long getIdCargador()   { return idCargador; }
    public void setIdCargador(long idCargador) {this.idCargador = idCargador;}

}