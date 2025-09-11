package com.sergiogps.bus_map_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergiogps.bus_map_api.entity.key.RolesPermisosId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles_permisos")
public class RolesPermisos {
    @EmbeddedId
    private RolesPermisosId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    @JsonIgnore
    private Roles rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permisoId")
    @JoinColumn(name = "permiso_id")
    @JsonIgnore
    private Permisos permiso;

    public RolesPermisosId getId() { return id; }
    public void setId(RolesPermisosId id) { this.id = id; }
    public Roles getRol() { return rol; }
    public void setRol(Roles rol) { this.rol = rol; }
    public Permisos getPermiso() { return permiso; }
    public void setPermiso(Permisos permiso) { this.permiso = permiso; }
}
