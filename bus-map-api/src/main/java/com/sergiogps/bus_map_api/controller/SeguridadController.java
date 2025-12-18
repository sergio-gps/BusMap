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

import com.sergiogps.bus_map_api.entity.Seguridad;
import com.sergiogps.bus_map_api.service.SeguridadService;

@RestController
@RequestMapping("/api/seguridad")
public class SeguridadController {
    private final SeguridadService service;

    public SeguridadController(SeguridadService service) { this.service = service; }

    @GetMapping
    public List<Seguridad> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Seguridad> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Seguridad> create(@RequestBody Seguridad body) {
        Seguridad created = service.create(body);
        return ResponseEntity.created(URI.create("/api/seguridad/" + created.getId())).body(created);
    }
}
