package com.sergiogps.bus_map_api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.dto.AddRoleRequestDTO;
import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.service.UsuariosService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    private final UsuariosService service;

    public UsuariosController(UsuariosService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuarios> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuarios> create(@RequestBody Usuarios body) {
        Usuarios created = service.create(body);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getUsuarioId())).body(created);
    }

    @PostMapping("/roles")
    public ResponseEntity<?> addRoleToUser(@RequestBody AddRoleRequestDTO request) {
        if (request.userId() == null || request.rol() == null || request.rol().isBlank()) {
            return ResponseEntity.badRequest().body("userId y rol son obligatorios");
        }

        try {
            Usuarios updated = service.addRoleToUser(request.userId(), request.rol());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/me")
    public ResponseEntity<Usuarios> updateUsuario(@RequestBody Usuarios body) {
        // Obtiene el usuario logueado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(service.updateSessionUser(currentPrincipalName, body));
    }

    @GetMapping("/me")
    public ResponseEntity<Usuarios> getUsuario() {
        // Obtiene el usuario logueado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(service.findByEmail(currentPrincipalName));
    }
}
