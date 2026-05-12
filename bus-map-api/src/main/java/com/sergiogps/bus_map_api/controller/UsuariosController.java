package com.sergiogps.bus_map_api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios, roles y permisos")
public class UsuariosController {
    private final UsuariosService service;

    public UsuariosController(UsuariosService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna lista de todos los usuarios en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class)))
    public List<Usuarios> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los datos de un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuarios> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
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
    @Operation(summary = "Agregar rol a usuario", description = "Asigna un nuevo rol a un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol agregado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - faltan datos requeridos o rol no existe")
    })
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
    @Operation(summary = "Eliminar rol de usuario", description = "Remueve un rol de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - faltan datos requeridos o rol no existe")
    })
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
    @Operation(summary = "Actualizar perfil del usuario", description = "Actualiza los datos del usuario actualmente autenticado. Requiere JWT token")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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
    @Operation(summary = "Obtener perfil del usuario", description = "Obtiene los datos del usuario actualmente autenticado. Requiere JWT token")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del usuario obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuarios.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Usuarios> getUsuario() {
        // Obtiene el usuario logueado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(service.findByEmail(currentPrincipalName));
    }

    /**
     * Endpoint para actualizar un usuario por su id
     * 
     * @param id el id del usuario a actualizar
     * @param body los datos actualizados del usuario
     * @return ResponseEntity con el usuario actualizado o 404 si no se encuentra
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> updateUsuarioById(@PathVariable Integer id, @RequestBody Usuarios body) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        body.setUsuarioId(id);
        Usuarios updated = service.update(body);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint para eliminar un usuario por su id
     * 
     * @param id el id del usuario a eliminar
     * @return ResponseEntity con estado 204 No Content si se eliminó correctamente, o 404 si no se encuentra
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
