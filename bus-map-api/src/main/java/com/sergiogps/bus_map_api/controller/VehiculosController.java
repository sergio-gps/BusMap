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

import com.sergiogps.bus_map_api.entity.Vehiculos;
import com.sergiogps.bus_map_api.service.VehiculosService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculosController {
    private final VehiculosService service;

    public VehiculosController(VehiculosService service) { this.service = service; }

    @GetMapping
    public List<Vehiculos> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculos> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Vehiculos> create(@RequestBody Vehiculos body) {
        Vehiculos created = service.create(body);
        return ResponseEntity.created(URI.create("/api/vehiculos/" + created.getVehiculoId())).body(created);
    }
}
