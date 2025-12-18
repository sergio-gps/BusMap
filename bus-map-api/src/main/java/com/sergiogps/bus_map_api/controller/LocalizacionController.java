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

import com.sergiogps.bus_map_api.entity.Localizacion;
import com.sergiogps.bus_map_api.service.LocalizacionService;

@RestController
@RequestMapping("/api/localizacion")
public class LocalizacionController {
    private final LocalizacionService service;

    public LocalizacionController(LocalizacionService service) { this.service = service; }

    @GetMapping
    public List<Localizacion> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Localizacion> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Localizacion> create(@RequestBody Localizacion body) {
        Localizacion created = service.create(body);
        return ResponseEntity.created(URI.create("/api/localizacion/" + created.getLocalizacionId())).body(created);
    }
}
