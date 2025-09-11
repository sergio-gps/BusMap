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

import com.sergiogps.bus_map_api.entity.Permisos;
import com.sergiogps.bus_map_api.service.PermisosService;

@RestController
@RequestMapping("/api/permisos")
public class PermisosController {
    private final PermisosService service;

    public PermisosController(PermisosService service) { this.service = service; }

    @GetMapping
    public List<Permisos> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Permisos> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Permisos> create(@RequestBody Permisos body) {
        Permisos created = service.create(body);
        return ResponseEntity.created(URI.create("/api/permisos/" + created.getPermisoId())).body(created);
    }
}
