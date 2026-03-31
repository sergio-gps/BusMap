package com.sergiogps.bus_map_api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parada {
    // Usamos @JsonProperty para mapear el campo "numero" del JSON a "id" en Java
    @JsonProperty("numero")
    private int id;

    private String nombre;

    // Ojo: En tu JSON es "latitude", no "latitud"
    @JsonProperty("latitude")
    private double latitud;

    @JsonProperty("longitude")
    private double longitud;

    private List<Integer> lineas = new ArrayList<>();

    // Constructor vacío necesario para Jackson
    public Parada() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public List<Integer> getLineas() {
        return lineas;
    }

    @JsonProperty("lineas")
    public void setLineasIntegers(List<Integer> lineas) {
        this.lineas = lineas == null ? new ArrayList<>() : new ArrayList<>(lineas);
    }
}
