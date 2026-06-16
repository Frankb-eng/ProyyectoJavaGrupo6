package org.tallerjava.ModuloPago.Interface.evento.out;

public class EventoPagoRealizado {

    private String tipoMedioPago;


    public EventoPagoRealizado() {}

    public EventoPagoRealizado(String tipoMedioPago) {
        this.tipoMedioPago = tipoMedioPago;
    }

    public String getTipoMedioPago() {
        return tipoMedioPago;
    }
}
