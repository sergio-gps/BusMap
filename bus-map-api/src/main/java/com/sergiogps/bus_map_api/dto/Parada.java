package com.sergiogps.bus_map_api.dto;

import java.util.List;

public class Parada {
    private int id;
    private String nombre;
    private double latitud;
    private double longitud;
    private List<Integer> lineas; // IDs de las líneas que pasan por aquí (ej: [2, 18])
    
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public double getLatitud() {
        return latitud;
    }
    public double getLongitud() {
        return longitud;
    }
    public List<Integer> getLineas() {
        return lineas;
    }
}
