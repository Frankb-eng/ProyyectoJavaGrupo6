package org.tallerjava.ModuloPago.Interface.remota.rest.dto;

import org.tallerjava.ModuloPago.dominio.MedioPagoPago;

public class NotificacionDTO {

    private int monto;
    private MedioPagoPago pago;

    public NotificacionDTO() {
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public MedioPagoPago getPago() {
        return pago;
    }

    public void setPago(MedioPagoPago pago) {
        this.pago = pago;
    }
}