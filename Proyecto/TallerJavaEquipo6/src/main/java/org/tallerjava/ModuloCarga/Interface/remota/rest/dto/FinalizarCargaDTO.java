package org.tallerjava.ModuloCarga.Interface.remota.rest.dto;

public class FinalizarCargaDTO {

    private long idCargador;
    private float consumoKwh;
    private int minutosDemora;

    public FinalizarCargaDTO() {}

    public long getIdCargador() { return idCargador; }
    public void setIdCargador(long idCargador) { this.idCargador = idCargador; }
    public float getConsumoKwh() { return consumoKwh; }
    public void setConsumoKwh(float consumoKwh) { this.consumoKwh = consumoKwh; }
    public int getMinutosDemora() { return minutosDemora; }
    public void setMinutosDemora(int minutosDemora) { this.minutosDemora = minutosDemora; }
}