package com.sergiogps.bus_map_api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;

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

    public void setLineasIntegers(List<Integer> lineas) {
        this.lineas = lineas == null ? new ArrayList<>() : new ArrayList<>(lineas);
    }

    /**
     * Acepta una lista de entidades Paradas y extrae sus IDs de línea.
     */
    @JsonProperty("lineas")
    public void setLineas(List<Paradas> paradasRaw) {
        this.lineas = new ArrayList<>();
        if (paradasRaw == null) {
            return;
        }

        for (Paradas parada : paradasRaw) {
            if (parada == null || parada.getLineas() == null) {
                continue;
            }

            for (Lineas linea : parada.getLineas()) {
                if (linea != null && linea.getLineaId() != null) {
                    this.lineas.add(linea.getLineaId());
                }
            }
        }
    }
}
