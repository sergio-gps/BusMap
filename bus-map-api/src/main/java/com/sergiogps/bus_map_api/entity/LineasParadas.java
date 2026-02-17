package com.sergiogps.bus_map_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergiogps.bus_map_api.entity.key.LineasParadasId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

public class LineasParadas {
    @EmbeddedId
    private LineasParadasId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lineaId")
    @JoinColumn(name = "linea_id")
    @JsonIgnore
    private Lineas linea;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("paradaId")
    @JoinColumn(name = "parada_id")
    @JsonIgnore
    private Paradas parada;

    public LineasParadasId getId() {
        return id;
    }

    public Lineas getLinea() {
        return linea;
    }

    public void setLinea(Lineas linea) {
        this.linea = linea;
    }

    public Paradas getParada() {
        return parada;
    }

    public void setParada(Paradas parada) {
        this.parada = parada;
    }

}
