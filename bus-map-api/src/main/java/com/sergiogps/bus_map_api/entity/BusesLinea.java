package com.sergiogps.bus_map_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergiogps.bus_map_api.entity.key.BusesLineaId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "buses_linea")
public class BusesLinea {
    @EmbeddedId
    private BusesLineaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vehiculoId")
    @JoinColumn(name = "vehiculo_id")
    @JsonIgnore
    private Vehiculos vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lineaId")
    @JoinColumn(name = "linea_id")
    @JsonIgnore
    private Lineas linea;

    public BusesLineaId getId() { return id; }
    public void setId(BusesLineaId id) { this.id = id; }
    public Vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculos vehiculo) { this.vehiculo = vehiculo; }
    public Lineas getLinea() { return linea; }
    public void setLinea(Lineas linea) { this.linea = linea; }
}
