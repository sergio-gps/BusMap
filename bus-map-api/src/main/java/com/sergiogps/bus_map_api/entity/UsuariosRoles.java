package com.sergiogps.bus_map_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergiogps.bus_map_api.entity.key.UsuariosRolesId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios_roles")
public class UsuariosRoles {
    @EmbeddedId
    private UsuariosRolesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    @JsonIgnore
    private Roles rol;

    public UsuariosRolesId getId() { return id; }
    public void setId(UsuariosRolesId id) { this.id = id; }
    public Usuarios getUsuario() { return usuario; }
    public void setUsuario(Usuarios usuario) { this.usuario = usuario; }
    public Roles getRol() { return rol; }
    public void setRol(Roles rol) { this.rol = rol; }
}
