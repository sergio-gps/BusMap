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
@Table(name = "historico_rutas")
public class HistoricoRutas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @JsonIgnore
    private Vehiculos vehiculo;

    @Convert(converter = GeoPointConverter.class)
    @Column(name = "punto_origen", columnDefinition = "point", nullable = false)
    private GeoPoint puntoOrigen;

    @Column(name = "destino_buscado", nullable = false)
    private String destinoBuscado;

    @Column(name = "fecha_ruta", nullable = false)
    private LocalDateTime fechaRuta;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculos vehiculo) { this.vehiculo = vehiculo; }
    public GeoPoint getPuntoOrigen() { return puntoOrigen; }
    public void setPuntoOrigen(GeoPoint puntoOrigen) { this.puntoOrigen = puntoOrigen; }
    public String getDestinoBuscado() { return destinoBuscado; }
    public void setDestinoBuscado(String destinoBuscado) { this.destinoBuscado = destinoBuscado; }
    public LocalDateTime getFechaRuta() { return fechaRuta; }
    public void setFechaRuta(LocalDateTime fechaRuta) { this.fechaRuta = fechaRuta; }
}
