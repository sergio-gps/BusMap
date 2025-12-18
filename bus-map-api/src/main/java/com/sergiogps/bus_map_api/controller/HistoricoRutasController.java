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

import com.sergiogps.bus_map_api.entity.HistoricoRutas;
import com.sergiogps.bus_map_api.service.HistoricoRutasService;

@RestController
@RequestMapping("/api/historico-rutas")
public class HistoricoRutasController {
    private final HistoricoRutasService service;

    public HistoricoRutasController(HistoricoRutasService service) { this.service = service; }

    @GetMapping
    public List<HistoricoRutas> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoRutas> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<HistoricoRutas> create(@RequestBody HistoricoRutas body) {
        HistoricoRutas created = service.create(body);
        return ResponseEntity.created(URI.create("/api/historico-rutas/" + created.getRutaId())).body(created);
    }
}
