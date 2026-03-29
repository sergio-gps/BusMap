package com.sergiogps.bus_map_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "paradas")
public class Paradas {

    // Columna de ID
    @Id
    @Column(name = "parada_id")
    private Integer paradaId;

    @Column(name = "nombre_parada", nullable = false, length = 100)
    private String nombreParada;
    private Double latitud;
    private Double longitud;

    @ManyToMany(mappedBy = "paradas")
    private List<Lineas> lineas = new ArrayList<>();

    // Getters y Setters
    public Integer getParadaId() {
        return paradaId;
    }

    public void setParadaId(Integer paradaId) {
        this.paradaId = paradaId;
    }

    public String getNombre() {
        return nombreParada;
    }

    public void setNombre(String nombre) {
        this.nombreParada = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public List<Lineas> getLineas() {
        return lineas;
    }

    public void setLineas(List<Lineas> lineas) {
        this.lineas = lineas;
    }
}