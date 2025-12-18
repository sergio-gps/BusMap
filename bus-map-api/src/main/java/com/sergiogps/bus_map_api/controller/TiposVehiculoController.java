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

import com.sergiogps.bus_map_api.entity.TiposVehiculo;
import com.sergiogps.bus_map_api.service.TiposVehiculoService;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TiposVehiculoController {
    private final TiposVehiculoService service;

    public TiposVehiculoController(TiposVehiculoService service) { this.service = service; }

    @GetMapping
    public List<TiposVehiculo> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<TiposVehiculo> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<TiposVehiculo> create(@RequestBody TiposVehiculo body) {
        TiposVehiculo created = service.create(body);
        return ResponseEntity.created(URI.create("/api/tipos-vehiculo/" + created.getTipoId())).body(created);
    }
}
