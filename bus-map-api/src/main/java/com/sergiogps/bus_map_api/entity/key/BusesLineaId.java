package com.sergiogps.bus_map_api.entity.key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BusesLineaId implements Serializable {
    @Column(name = "vehiculo_id")
    private Integer vehiculoId;

    @Column(name = "linea_id")
    private Integer lineaId;

    public BusesLineaId() {}
    public BusesLineaId(Integer vehiculoId, Integer lineaId) {
        this.vehiculoId = vehiculoId;
        this.lineaId = lineaId;
    }

    public Integer getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Integer vehiculoId) { this.vehiculoId = vehiculoId; }
    public Integer getLineaId() { return lineaId; }
    public void setLineaId(Integer lineaId) { this.lineaId = lineaId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusesLineaId that = (BusesLineaId) o;
        return Objects.equals(vehiculoId, that.vehiculoId) && Objects.equals(lineaId, that.lineaId);
    }

    @Override
    public int hashCode() { return Objects.hash(vehiculoId, lineaId); }
}
