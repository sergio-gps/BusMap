package com.sergiogps.bus_map_api.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "lineas")
public class Lineas {
    @Id
    @Column(name = "linea_id")
    private Integer lineaId;

    @Column(nullable = false)
    private String nombre;
    
    private String origen;
    private String destino;
    private String color;

    // Relación bidireccional
    @ManyToMany(mappedBy = "lineas")
    private List<Paradas> paradas;

    // Constructor vacío (Obligatorio para JPA)
    public Lineas() {}

    // Getters y Setters
    public Integer getLineaId() { return lineaId; }
    public void setLineaId(Integer lineaId) { this.lineaId = lineaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<Paradas> getParadas() { return paradas; }
    public void setParadas(List<Paradas> paradas) { this.paradas = paradas; }
}