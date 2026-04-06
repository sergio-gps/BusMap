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

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;
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
        URI location = Objects.requireNonNull(URI.create("/api/lineas/" + created.getLineaId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lineas> update(@PathVariable Integer id, @RequestBody Lineas body) {
        return service.update(id, body)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/paradas")
    public ResponseEntity<List<Paradas>> getParadasByLinea(@PathVariable Integer id) {
        return service.findParadasByLineaId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{lineaId}/paradas/{paradaId}")
    public ResponseEntity<Lineas> addParadaToLinea(@PathVariable Integer lineaId, @PathVariable Integer paradaId) {
        return service.addParadaToLinea(lineaId, paradaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{lineaId}/paradas/{paradaId}")
    public ResponseEntity<Lineas> removeParadaFromLinea(@PathVariable Integer lineaId, @PathVariable Integer paradaId) {
        return service.removeParadaFromLinea(lineaId, paradaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importJson(@RequestBody List<Lineas> lineas) {
        int procesadas = service.importLineas(lineas);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processed", procesadas);

        response.put("success", true);
        response.put("data", data);
        response.put("error", null);
        return ResponseEntity.ok(response);
    }
}
