package com.sergiogps.bus_map_api.entity.key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RolesPermisosId implements Serializable {
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "permiso_id")
    private Integer permisoId;

    public RolesPermisosId() {}
    public RolesPermisosId(Integer rolId, Integer permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    public Integer getRolId() { return rolId; }
    public void setRolId(Integer rolId) { this.rolId = rolId; }
    public Integer getPermisoId() { return permisoId; }
    public void setPermisoId(Integer permisoId) { this.permisoId = permisoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolesPermisosId that = (RolesPermisosId) o;
        return Objects.equals(rolId, that.rolId) && Objects.equals(permisoId, that.permisoId);
    }

    @Override
    public int hashCode() { return Objects.hash(rolId, permisoId); }
}
