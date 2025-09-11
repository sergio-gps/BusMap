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

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.service.LineasService;

@RestController
@RequestMapping("/api/lineas")
public class LineasController {
    private final LineasService service;

    public LineasController(LineasService service) { this.service = service; }

    @GetMapping
    public List<Lineas> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Lineas> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lineas> create(@RequestBody Lineas body) {
        Lineas created = service.create(body);
        return ResponseEntity.created(URI.create("/api/lineas/" + created.getLineaId())).body(created);
    }
}
