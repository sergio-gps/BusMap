package com.sergiogps.bus_map_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParadaSecuencia {
    private int id;

    @JsonProperty("linea_id")
    private int lineaId;

    @JsonProperty("parada_id")
    private int paradaId;

    @JsonProperty("siguiente_parada")
    private Integer siguienteParada;

    public ParadaSecuencia() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLineaId() { return lineaId; }
    public void setLineaId(int lineaId) { this.lineaId = lineaId; }

    public int getParadaId() { return paradaId; }
    public void setParadaId(int paradaId) { this.paradaId = paradaId; }

    public Integer getSiguienteParada() { return siguienteParada; }
    public void setSiguienteParada(Integer siguienteParada) { this.siguienteParada = siguienteParada; }
}