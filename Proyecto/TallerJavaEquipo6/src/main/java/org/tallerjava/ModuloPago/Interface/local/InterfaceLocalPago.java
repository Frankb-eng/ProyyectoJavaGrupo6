package org.tallerjava.ModuloPago.Interface.local;

import jakarta.ws.rs.core.Response;

public interface InterfaceLocalPago {
    public boolean pagarCarga(String cedula, int importe, Long idMedioPago);
    boolean clienteHabilitadoParaCargar(String cedula);
}
