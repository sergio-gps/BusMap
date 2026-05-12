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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/lineas")
@Tag(name = "Líneas", description = "Gestión de líneas de transporte")
public class LineasController {
    private final LineasService service;

    public LineasController(LineasService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Obtener todas las líneas", description = "Retorna lista de todas las líneas de transporte")
    @ApiResponse(responseCode = "200", description = "Lista de líneas obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class)))
    public List<Lineas> all() { return service.findAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener línea por ID", description = "Obtiene los datos de una línea específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class))),
            @ApiResponse(responseCode = "404", description = "Línea no encontrada")
    })
    public ResponseEntity<Lineas> byId(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva línea", description = "Crea una nueva línea de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Lineas> create(@RequestBody Lineas body) {
        Lineas created = service.create(body);
        URI location = Objects.requireNonNull(URI.create("/api/lineas/" + created.getLineaId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar línea", description = "Actualiza los datos de una línea existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class))),
            @ApiResponse(responseCode = "404", description = "Línea no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Lineas> update(@PathVariable Integer id, @RequestBody Lineas body) {
        return service.update(id, body)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar línea", description = "Elimina una línea de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Línea eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Línea no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/paradas")
    @Operation(summary = "Obtener paradas de una línea", description = "Retorna todas las paradas asociadas a una línea de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paradas obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Paradas.class))),
            @ApiResponse(responseCode = "404", description = "Línea no encontrada")
    })
    public ResponseEntity<List<Paradas>> getParadasByLinea(@PathVariable Integer id) {
        return service.findParadasByLineaId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{lineaId}/paradas/{paradaId}")
    @Operation(summary = "Agregar parada a línea", description = "Asocia una parada existente a una línea de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parada agregada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class))),
            @ApiResponse(responseCode = "404", description = "Línea o parada no encontrada")
    })
    public ResponseEntity<Lineas> addParadaToLinea(@PathVariable Integer lineaId, @PathVariable Integer paradaId) {
        return service.addParadaToLinea(lineaId, paradaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{lineaId}/paradas/{paradaId}")
    @Operation(summary = "Remover parada de línea", description = "Desasocia una parada de una línea de transporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parada removida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lineas.class))),
            @ApiResponse(responseCode = "404", description = "Línea o parada no encontrada")
    })
    public ResponseEntity<Lineas> removeParadaFromLinea(@PathVariable Integer lineaId, @PathVariable Integer paradaId) {
        return service.removeParadaFromLinea(lineaId, paradaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/import")
    @Operation(summary = "Importar líneas desde JSON", description = "Importa múltiples líneas desde un archivo JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Líneas importadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos JSON inválidos")
    })
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
