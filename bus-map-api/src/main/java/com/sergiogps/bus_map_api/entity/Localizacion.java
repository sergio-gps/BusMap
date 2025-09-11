package com.sergiogps.bus_map_api.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergiogps.bus_map_api.entity.converter.GeoPointConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "localizacion")
public class Localizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @JsonIgnore
    private Vehiculos vehiculo;

    @Convert(converter = GeoPointConverter.class)
    @Column(name = "coordenadas", columnDefinition = "point", nullable = false)
    private GeoPoint coordenadas;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculos vehiculo) { this.vehiculo = vehiculo; }
    public GeoPoint getCoordenadas() { return coordenadas; }
    public void setCoordenadas(GeoPoint coordenadas) { this.coordenadas = coordenadas; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
