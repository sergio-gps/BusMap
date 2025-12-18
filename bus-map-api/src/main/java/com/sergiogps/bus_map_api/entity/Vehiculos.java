package com.sergiogps.bus_map_api.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
public class Vehiculos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehiculo_id")
    private Integer vehiculoId;

    // Relación con TiposVehiculo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TiposVehiculo tipo;

    // Relación 1:1 con la información técnica
    // 'cascade = ALL' para que al crear un vehículo se pueda crear su info a la vez
    // 'orphanRemoval = true' para que si se borra el vehículo, se borre su info
    @OneToOne(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private VehiculosInfo info;

    // Relación N:M con Líneas
    @ManyToMany
    @JoinTable(
        name = "buses_linea",
        joinColumns = @JoinColumn(name = "vehiculo_id"),
        inverseJoinColumns = @JoinColumn(name = "linea_id")
    )
    private List<Lineas> lineas;

    // Constructor vacío (obligatorio)
    public Vehiculos() {}

    // Getters y Setters
    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Integer vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public TiposVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TiposVehiculo tipo) {
        this.tipo = tipo;
    }

    public VehiculosInfo getInfo() {
        return info;
    }

    public void setInfo(VehiculosInfo info) {
        this.info = info;
    }

    public List<Lineas> getLineas() {
        return lineas;
    }

    public void setLineas(List<Lineas> lineas) {
        this.lineas = lineas;
    }
}