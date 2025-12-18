package com.sergiogps.bus_map_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "seguridad")
public class Seguridad {
    @Id
    private Integer usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuarios usuario;

    @Column(nullable = false)
    private String password;

    public Seguridad() {}

    public Integer getId() { return usuarioId; }
    public void setId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Usuarios getUsuario() { return usuario; }
    public void setUsuario(Usuarios usuario) { this.usuario = usuario; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
