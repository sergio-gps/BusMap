package com.sergiogps.bus_map_api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.service.UsuariosService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    private final UsuariosService service;

    public UsuariosController(UsuariosService service) { this.service = service; }

    @GetMapping
    public List<Usuarios> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Usuarios> create(@RequestBody Usuarios body) {
        Usuarios created = service.create(body);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getUsuarioId())).body(created);
    }
}
