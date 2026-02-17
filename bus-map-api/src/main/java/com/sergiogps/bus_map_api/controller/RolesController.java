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

import com.sergiogps.bus_map_api.entity.Roles;
import com.sergiogps.bus_map_api.service.RolesService;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    private final RolesService service;

    public RolesController(RolesService service) { this.service = service; }

    @GetMapping
    public List<Roles> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Roles> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Roles> create(@RequestBody Roles body) {
        Roles created = service.create(body);
        return ResponseEntity.created(URI.create("/api/roles/" + created.getRolId())).body(created);
    }
}
