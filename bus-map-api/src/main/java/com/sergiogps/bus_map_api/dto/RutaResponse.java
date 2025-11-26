package com.sergiogps.bus_map_api.dto;

import java.util.List;

public class RutaResponse {
    private List<String> lineasSugeridas; // Ej: ["Línea 2", "Línea 18"]
    private int tiempoEstimadoMinutos;
    private List<String> instrucciones;

    public List<String> getLineasSugeridas() {
        return lineasSugeridas;
    }
    public int getTiempoEstimadoMinutos() {
        return tiempoEstimadoMinutos;
    }
    public List<String> getInstrucciones() {
        return instrucciones;
    }

    public void setLineasSugeridas(List<String> lineasSugeridas) {
        this.lineasSugeridas = lineasSugeridas;
    }

    public void setTiempoEstimadoMinutos(int tiempoEstimadoMinutos) {
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
    }

    public void setInstrucciones(List<String> instrucciones) {
        this.instrucciones = instrucciones;
    }
}
