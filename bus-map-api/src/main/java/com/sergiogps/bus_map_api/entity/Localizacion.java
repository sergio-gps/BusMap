package com.sergiogps.bus_map_api.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "localizaciones")
public class Localizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "localizacion_id")
    private Integer localizacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @JsonIgnore
    private Vehiculos vehiculo;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "actualizado", nullable = false)
    private LocalDateTime actualizado;

    public Integer getLocalizacionId() { return localizacionId; }
    public void setLocalizacionId(Integer localizacionId) { this.localizacionId = localizacionId; }

    // Compatibilidad temporal con código legacy.
    public Integer getId() { return localizacionId; }
    public void setId(Integer id) { this.localizacionId = id; }
    public Vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculos vehiculo) { this.vehiculo = vehiculo; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public LocalDateTime getActualizado() { return actualizado; }
    public void setActualizado(LocalDateTime actualizado) { this.actualizado = actualizado; }

    // Compatibilidad temporal con código legacy.
    public LocalDateTime getUpdatedAt() { return actualizado; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.actualizado = updatedAt; }
}
