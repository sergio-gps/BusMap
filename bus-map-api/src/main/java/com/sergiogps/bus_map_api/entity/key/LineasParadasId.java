package com.sergiogps.bus_map_api.entity.key;

import java.util.Objects;

import jakarta.persistence.Column;

public class LineasParadasId {
    @Column(name = "linea_id")
    private Integer lineaId;

    @Column(name = "parada_id")
    private Integer paradaId;

    public LineasParadasId() {}
    public LineasParadasId(Integer lineaId, Integer paradaId) {
        this.lineaId = lineaId;
        this.paradaId = paradaId;
    }

    public Integer getLineaId() { return lineaId; }
    public void setLineaId(Integer lineaId) { this.lineaId = lineaId; }
    public Integer getParadaId() { return paradaId; }
    public void setParadaId(Integer paradaId) { this.paradaId = paradaId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineasParadasId that = (LineasParadasId) o;
        return Objects.equals(lineaId, that.lineaId) && Objects.equals(paradaId, that.paradaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineaId, paradaId);
    }
}
