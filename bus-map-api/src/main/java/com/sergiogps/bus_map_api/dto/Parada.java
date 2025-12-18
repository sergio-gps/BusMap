package com.sergiogps.bus_map_api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parada {

    @JsonProperty("numero") 
    private int id;
    
    private String nombre;
    
    @JsonProperty("latitude")
    private double latitud;
    
    @JsonProperty("longitude")
    private double longitud;
    
    private List<Integer> lineas;

    // Constructor vacío necesario para Jackson
    public Parada() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public List<Integer> getLineas() { return lineas; }
    public void setLineas(List<Integer> lineas) { this.lineas = lineas; }
}
