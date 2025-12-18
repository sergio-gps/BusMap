package com.sergiogps.bus_map_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos_info")
public class VehiculosInfo {

    @Id
    @Column(name = "vehiculo_id")
    private Integer vehiculoId;

    // Relación 1:1 compartiendo el ID
    @OneToOne
    @MapsId
    @JoinColumn(name = "vehiculo_id")
    private Vehiculos vehiculo;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(name = "marca_modelo")
    private String marcaModelo;

    private Integer capacidad;

    @Column(nullable = false)
    private Boolean activo;

    // Constructor vacío
    public VehiculosInfo() {}

    // Getters y Setters
    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Integer vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public Vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}