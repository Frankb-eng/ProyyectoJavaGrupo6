package org.tallerjava.ModuloPago.Interface.remota.rest.dto;

import org.tallerjava.ModuloPago.dominio.*;
import org.tallerjava.ModuloCliente.dominio.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PagoDTO {

    private Long id;
    private int importe;
    private LocalDate fecha;
    private LocalDateTime hora;
    private Long idMedioPago;
    private String idCliente;
    private EstadoPago estado;

    public PagoDTO() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getImporte() { return importe; }

    public void setImporte(int importe) { this.importe = importe; }

    public LocalDate getFecha() { return fecha; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getHora() { return hora; }

    public void setHora(LocalDateTime hora) { this.hora = hora; }

    public Long getIdMedioPago() {return idMedioPago;}

    public void setIdMedioPago(Long idMedioPago) {this.idMedioPago = idMedioPago;}

    public String getIdCliente() {return idCliente;}

    public void setIdCliente(String idCliente) {this.idCliente = idCliente;}

    public EstadoPago getEstado() {return estado;}

    public void setEstado(EstadoPago estado) {this.estado = estado;}

    public Pago build(){
        Pago pago = new Pago(this.idCliente,this.importe,this.idMedioPago);
        pago.setId(this.id);
        pago.setFecha(LocalDate.now());
        pago.setHora(LocalDateTime.now());
        pago.setEstado(this.estado);
        return pago;
    }

    public static PagoDTO convertirDTO(Pago pago){
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setImporte(pago.getImporte());
        dto.setFecha(pago.getFecha());
        dto.setHora(pago.getHora());
        return dto;
    }
}
