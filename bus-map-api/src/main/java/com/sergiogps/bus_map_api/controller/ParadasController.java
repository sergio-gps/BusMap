package com.sergiogps.bus_map_api.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.sergiogps.bus_map_api.dto.Parada;
import com.sergiogps.bus_map_api.service.ParadasService;

@RestController
@RequestMapping("/api/paradas")
public class ParadasController {

    private final ParadasService service;

    public ParadasController(ParadasService service) {
        this.service = service;
    }

    @GetMapping
    public List<Parada> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parada> byId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Parada> create(@RequestBody Parada body) {
        Parada created = service.create(body);
        URI location = Objects.requireNonNull(URI.create("/api/paradas/" + created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parada> update(@PathVariable Integer id, @RequestBody Parada body) {
        return service.update(id, body)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importJson(@RequestBody List<Parada> paradas) {
        int procesadas = service.importParadas(paradas);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processed", procesadas);

        response.put("success", true);
        response.put("data", data);
        response.put("error", null);
        return ResponseEntity.ok(response);
    }
}
