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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/paradas")
@Tag(name = "Paradas", description = "Gestión de paradas de transporte público")
public class ParadasController {

    private final ParadasService service;

    public ParadasController(ParadasService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las paradas", description = "Retorna lista de todas las paradas de transporte")
    @ApiResponse(responseCode = "200", description = "Lista de paradas obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Parada.class)))
    public List<Parada> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener parada por ID", description = "Obtiene los datos de una parada específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parada encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Parada.class))),
            @ApiResponse(responseCode = "404", description = "Parada no encontrada")
    })
    public ResponseEntity<Parada> byId(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva parada", description = "Crea una nueva parada de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parada creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Parada.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Parada> create(@RequestBody Parada body) {
        Parada created = service.create(body);
        URI location = Objects.requireNonNull(URI.create("/api/paradas/" + created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parada", description = "Actualiza los datos de una parada existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parada actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Parada.class))),
            @ApiResponse(responseCode = "404", description = "Parada no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Parada> update(@PathVariable Integer id, @RequestBody Parada body) {
        return service.update(id, body)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar parada", description = "Elimina una parada de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parada eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Parada no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    @Operation(summary = "Importar paradas desde JSON", description = "Importa múltiples paradas desde un archivo JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paradas importadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos JSON inválidos")
    })
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
