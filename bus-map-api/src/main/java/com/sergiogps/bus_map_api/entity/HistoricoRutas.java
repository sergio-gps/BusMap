package com.sergiogps.bus_map_api.entity;

import java.time.LocalDateTime;

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
@Table(name = "historico_rutas")
public class HistoricoRutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruta_id")
    private Integer rutaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculos vehiculo;

    @Column(name = "origen_lat", nullable = false)
    private Double origenLat;

    @Column(name = "origen_lon", nullable = false)
    private Double origenLon;

    @Column(name = "destino_buscado", nullable = false)
    private String destinoBuscado;

    @Column(name = "fecha_ruta", nullable = false)
    private LocalDateTime fechaRuta;

    public HistoricoRutas() {}

    // Getters y Setters
    public Integer getRutaId() { return rutaId; }
    public void setRutaId(Integer rutaId) { this.rutaId = rutaId; }

    public Vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculos vehiculo) { this.vehiculo = vehiculo; }

    public Double getOrigenLat() { return origenLat; }
    public void setOrigenLat(Double origenLat) { this.origenLat = origenLat; }

    public Double getOrigenLon() { return origenLon; }
    public void setOrigenLon(Double origenLon) { this.origenLon = origenLon; }

    public String getDestinoBuscado() { return destinoBuscado; }
    public void setDestinoBuscado(String destinoBuscado) { this.destinoBuscado = destinoBuscado; }

    public LocalDateTime getFechaRuta() { return fechaRuta; }
    public void setFechaRuta(LocalDateTime fechaRuta) { this.fechaRuta = fechaRuta; }
}