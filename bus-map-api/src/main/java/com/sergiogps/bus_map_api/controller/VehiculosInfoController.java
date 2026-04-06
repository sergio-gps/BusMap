package com.sergiogps.bus_map_api.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.entity.VehiculosInfo;
import com.sergiogps.bus_map_api.service.VehiculosInfoService;

@RestController
@RequestMapping("/api/vehiculos-info")
public class VehiculosInfoController {
    private final VehiculosInfoService service;

    public VehiculosInfoController(VehiculosInfoService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiculosInfo> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculosInfo> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehiculosInfo> create(@RequestBody VehiculosInfo body) {
        VehiculosInfo created = service.create(body);
        URI location = Objects.requireNonNull(URI.create("/api/vehiculos-info/" + created.getVehiculoId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculosInfo> update(@PathVariable Integer id, @RequestBody VehiculosInfo body) {
        return service.update(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
