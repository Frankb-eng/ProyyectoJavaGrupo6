package org.tallerjava.ModuloCarga.Interface.remota.rest.dto;

public class IniciarCargaDTO {

    private String cedulaCliente;
    private long idCargador;
    private long idMedioPago;

    public IniciarCargaDTO() {}

    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }
    public long getIdCargador() { return idCargador; }
    public void setIdCargador(long idCargador) { this.idCargador = idCargador; }
    public long getIdMedioPago() { return idMedioPago; }
    public void setIdMedioPago(long idMedioPago) { this.idMedioPago = idMedioPago; }
}