package org.tallerjava.ModuloCarga.Interface.evento.out;

public class EventoCargaFinalizada {
    private long idCarga,idMedioPago, importe;
    private String clienteId;

    public EventoCargaFinalizada(long idCarga, String clienteId, long idMedioPago, long importe) {
        this.idCarga = idCarga;
        this.clienteId = clienteId;
        this.idMedioPago = idMedioPago;
        this.importe = importe;
    }

     public EventoCargaFinalizada() {} // constructor vacio — usado por el modulo de monitoreo
    // que solo necesita saber que ocurrio, no los datos

    
    public long getIdCarga() {
        return idCarga;
    }

    public void setIdCarga(long idCarga) {
        this.idCarga = idCarga;
    }

    public String getCedulaCliente() {
        return clienteId;
    }

    public void setCedulaCliente(String clienteId) {
        this.clienteId = clienteId;
    }

    public long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(long idMedioPago) {
        this.idMedioPago = idMedioPago;
    }

    public long getImporte() {
        return importe;
    }

    public void setImporte(long importe) {
        this.importe = importe;
    }
}
