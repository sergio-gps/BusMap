package com.sergiogps.bus_map_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.entity.RolesPermisos;
import com.sergiogps.bus_map_api.entity.key.RolesPermisosId;
import com.sergiogps.bus_map_api.service.RolesPermisosService;

@RestController
@RequestMapping("/api/roles-permisos")
public class RolesPermisosController {
    private final RolesPermisosService service;

    public RolesPermisosController(RolesPermisosService service) { this.service = service; }

    @GetMapping
    public List<RolesPermisos> all() { return service.findAll(); }

    @GetMapping("/{rolId}/{permisoId}")
    public ResponseEntity<RolesPermisos> byId(@PathVariable Integer rolId, @PathVariable Integer permisoId) {
        return service.findById(new RolesPermisosId(rolId, permisoId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RolesPermisos create(@RequestBody RolesPermisos body) { return service.create(body); }
}
