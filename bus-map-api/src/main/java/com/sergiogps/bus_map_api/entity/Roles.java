package com.sergiogps.bus_map_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "rol_name", nullable = false, unique = true)
    private String rolName;

    @ManyToMany
    @JoinTable(name = "roles_permisos", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "rol_id"), inverseJoinColumns = @JoinColumn(name = "permiso_id"))
    private List<Permisos> permisos = new ArrayList<>();

    @ManyToMany(mappedBy = "roles")
    private List<Usuarios> usuarios = new ArrayList<>();

    // Getters and Setters
    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public String getRolName() {
        return rolName;
    }

    public void setRolName(String rolName) {
        this.rolName = rolName;
    }

    public List<Permisos> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permisos> permisos) {
        this.permisos = permisos;
    }
}
