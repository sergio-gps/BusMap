package com.sergiogps.bus_map_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.entity.UsuariosRoles;
import com.sergiogps.bus_map_api.entity.key.UsuariosRolesId;
import com.sergiogps.bus_map_api.service.UsuariosRolesService;

@RestController
@RequestMapping("/api/usuarios-roles")
public class UsuariosRolesController {
    private final UsuariosRolesService service;

    public UsuariosRolesController(UsuariosRolesService service) { this.service = service; }

    @GetMapping
    public List<UsuariosRoles> all() { return service.findAll(); }

    @GetMapping("/{usuarioId}/{rolId}")
    public ResponseEntity<UsuariosRoles> byId(@PathVariable Integer usuarioId, @PathVariable Integer rolId) {
        return service.findById(new UsuariosRolesId(usuarioId, rolId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsuariosRoles create(@RequestBody UsuariosRoles body) { return service.create(body); }
}
