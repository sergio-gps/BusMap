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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.dto.VehiculoAdminDTO;
import com.sergiogps.bus_map_api.dto.VehiculoAdminRequestDTO;
import com.sergiogps.bus_map_api.entity.Vehiculos;
import com.sergiogps.bus_map_api.service.VehiculosService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculosController {
    private final VehiculosService service;

    public VehiculosController(VehiculosService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiculoAdminDTO> all(@RequestParam(required = false) String query) {
        if (query == null || query.isBlank()) {
            return service.findAllAdmin();
        }
        return service.searchAdmin(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoAdminDTO> byId(@PathVariable Integer id) {
        return service.findAdminById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehiculoAdminDTO> create(@RequestBody VehiculoAdminRequestDTO body) {
        VehiculoAdminDTO created = service.createAdmin(body);
        URI location = Objects.requireNonNull(URI.create("/api/vehiculos/" + created.vehiculoId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoAdminDTO> update(@PathVariable Integer id, @RequestBody VehiculoAdminRequestDTO body) {
        return service.updateAdmin(id, body)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteAdminById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importJson(@RequestBody List<Vehiculos> vehiculos) {
        int procesadas = service.importVehiculos(vehiculos);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processed", procesadas);

        response.put("success", true);
        response.put("data", data);
        response.put("error", null);
        return ResponseEntity.ok(response);
    }
}
