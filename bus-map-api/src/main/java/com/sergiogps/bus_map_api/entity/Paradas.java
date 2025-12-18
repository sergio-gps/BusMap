package com.sergiogps.bus_map_api.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "paradas")
public class Paradas {

    @Id
    @Column(name = "parada_id")
    private Integer paradaId;

    @Column(name = "nombre_parada", nullable = false, length = 100)
    private String nombreParada;
    
    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
    
    // Mapeo de la relación Muchos a Muchos
    @ManyToMany
    @JoinTable(
        name = "parada_linea",
        joinColumns = @JoinColumn(name = "parada_id"),
        inverseJoinColumns = @JoinColumn(name = "linea_id")
    )
    private List<Lineas> lineas; 

    // Constructor vacío obligatorio para JPA
    public Paradas() {}

    public Integer getParadaId() {
        return paradaId;
    }

    public void setParadaId(Integer paradaId) {
        this.paradaId = paradaId;
    }

    public String getNombreParada() {
        return nombreParada;
    }

    public void setNombreParada(String nombreParada) {
        this.nombreParada = nombreParada;
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