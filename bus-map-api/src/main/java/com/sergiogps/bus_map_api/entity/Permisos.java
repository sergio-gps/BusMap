package com.sergiogps.bus_map_api.entity;

import java.util.ArrayList;
import java.util.List;

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
    private List<Roles> roles = new ArrayList<>();

    // Getters and Setters
    public Integer getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Integer permisoId) {
        this.permisoId = permisoId;
    }

    public String getPermisoName() {
        return permisoName;
    }

    public void setPermisoName(String permisoName) {
        this.permisoName = permisoName;
    }

    public List<Roles> getRoles() {
        return roles;
    }
    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}
