package com.sergiogps.bus_map_api.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos_vehiculo")
public class TiposVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_id")
    private Integer tipoId;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Relación inversa: Un tipo puede tener muchos vehículos asociados
    // El mappedBy apunta al nombre del campo 'tipo' en la clase Vehiculos
    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private List<Vehiculos> vehiculos;

    // Constructor vacío
    public TiposVehiculo() {}

    // Getters y Setters
    public Integer getTipoId() {
        return tipoId;
    }

    public void setTipoId(Integer tipoId) {
        this.tipoId = tipoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Vehiculos> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculos> vehiculos) {
        this.vehiculos = vehiculos;
    }
}