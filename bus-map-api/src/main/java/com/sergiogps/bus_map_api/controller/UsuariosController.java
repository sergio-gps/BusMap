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

import com.sergiogps.bus_map_api.dto.RoleRequestDTO;
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

    /**
     * Endpoint para agregar un rol a un usuario
     * 
     * @param request objeto que contiene el userId y el rol a agregar
     * @return ResponseEntity con el usuario actualizado o un mensaje de error si no se pudo agregar el rol
     */
    @PostMapping("/roles/add")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleRequestDTO request) {
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

    /**
     * Endpoint para eliminar un rol de un usuario
     * 
     * @param request objeto que contiene el userId y el rol a eliminar
     * @return ResponseEntity con el usuario actualizado o un mensaje de error si no se pudo eliminar el rol
     */
    @PostMapping("/roles/remove")
    public ResponseEntity<?> removeRoleFromUser(@RequestBody RoleRequestDTO request) {
        if (request.userId() == null || request.rol() == null || request.rol().isBlank()) {
            return ResponseEntity.badRequest().body("userId y rol son obligatorios");
        }

        try {
            Usuarios updated = service.removeRoleFromUser(request.userId(), request.rol());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Endpoint para actualizar los datos del usuario logueado
     * 
     * @param body los datos actualizados del usuario
     * @return ResponseEntity con el usuario actualizado
     */
    @PutMapping("/me")
    public ResponseEntity<Usuarios> updateUsuario(@RequestBody Usuarios body) {
        // Obtiene el usuario logueado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(service.updateSessionUser(currentPrincipalName, body));
    }

    /**
     * Endpoint para obtener los datos del usuario logueado
     * 
     * @return  ResponseEntity con los datos del usuario logueado
     */
    @GetMapping("/me")
    public ResponseEntity<Usuarios> getUsuario() {
        // Obtiene el usuario logueado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(service.findByEmail(currentPrincipalName));
    }
}
