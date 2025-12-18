package com.sergiogps.bus_map_api.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "permisos")
public class Permisos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_id")
    private Integer permisoId;

    @Column(name = "permiso_name", nullable = false, unique = true)
    private String permisoName;

    @ManyToMany(mappedBy = "permisos")
    private Set<Roles> roles;

    public Permisos() {}

    public Integer getPermisoId() { return permisoId; }
    public void setPermisoId(Integer permisoId) { this.permisoId = permisoId; }
    public String getPermisoName() { return permisoName; }
    public void setPermisoName(String permisoName) { this.permisoName = permisoName; }
}
