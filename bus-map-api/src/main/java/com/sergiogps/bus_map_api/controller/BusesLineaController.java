package com.sergiogps.bus_map_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.entity.BusesLinea;
import com.sergiogps.bus_map_api.entity.key.BusesLineaId;
import com.sergiogps.bus_map_api.service.BusesLineaService;

@RestController
@RequestMapping("/api/buses-linea")
public class BusesLineaController {
    private final BusesLineaService service;

    public BusesLineaController(BusesLineaService service) { this.service = service; }

    @GetMapping
    public List<BusesLinea> all() { return service.findAll(); }

    @GetMapping("/{vehiculoId}/{lineaId}")
    public ResponseEntity<BusesLinea> byId(@PathVariable Integer vehiculoId, @PathVariable Integer lineaId) {
        return service.findById(new BusesLineaId(vehiculoId, lineaId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BusesLinea create(@RequestBody BusesLinea body) { return service.create(body); }
}
