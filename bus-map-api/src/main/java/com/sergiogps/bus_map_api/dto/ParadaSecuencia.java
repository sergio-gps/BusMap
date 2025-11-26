package com.sergiogps.bus_map_api.dto;

public class ParadaSecuencia {
    private int id; // ID secuencial
    private int lineaId;
    private int paradaId;
    private Integer siguienteParadaId;

    public int getId() {
        return id;
    }
    public int getLineaId() {
        return lineaId;
    }
    public int getParadaId() {
        return paradaId;
    }
    public Integer getSiguienteParadaId() {
        return siguienteParadaId;
    }
}