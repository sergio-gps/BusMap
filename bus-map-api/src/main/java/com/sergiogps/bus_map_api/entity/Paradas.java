package com.sergiogps.bus_map_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    
    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
    
    // El campo 'lineas' (List<Integer>) requiere otro mapeo
    
    public Integer getId() {
        return paradaId;
    }

    public void setId(Integer id) {
        this.paradaId = id;
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
}