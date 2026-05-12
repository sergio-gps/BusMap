package com.sergiogps.bus_map_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.dto.HealthDto;
import com.sergiogps.bus_map_api.service.HealthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Salud de la API", description = "Endpoints para monitoreo y verificación de la salud de la API")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Comprehensive health check endpoint
     * GET /api/health
     */
    @GetMapping
    @Operation(
            summary = "Verificación completa de salud",
            description = "Retorna información detallada sobre el estado de la API y sus dependencias"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API en funcionamiento normal",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HealthDto.class))),
            @ApiResponse(responseCode = "503", description = "API no está disponible o servicio no disponible",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HealthDto.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al verificar salud")
    })
    public ResponseEntity<HealthDto> getHealth() {
        try {
            HealthDto healthDto = healthService.getHealthStatus();

            // Return appropriate HTTP status based on health
            if ("UP".equals(healthDto.getStatus())) {
                return ResponseEntity.ok(healthDto);
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(healthDto);
            }

        } catch (Exception e) {
            // Create error response
            HealthDto errorHealth = new HealthDto("DOWN", "Health check error: " + e.getMessage());
            errorHealth.setVersion("1.0.0");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorHealth);
        }
    }

    /**
     * Simple health check endpoint for basic monitoring
     * GET /api/health/status
     */
    @GetMapping("/status")
    @Operation(
            summary = "Estado simple de salud",
            description = "Retorna un estado simple (UP o DOWN) de la API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API en funcionamiento - retorna UP"),
            @ApiResponse(responseCode = "503", description = "API no disponible - retorna DOWN"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<String> getSimpleHealth() {
        try {
            String status = healthService.getSimpleStatus();

            if ("UP".equals(status)) {
                return ResponseEntity.ok(status);
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DOWN");
        }
    }

    /**
     * Ping endpoint for basic connectivity test
     * GET /api/health/ping
     */
    @GetMapping("/ping")
    @Operation(
            summary = "Prueba de conectividad básica",
            description = "Endpoint simple para verificar conectividad básica, retorna 'pong'"
    )
    @ApiResponse(responseCode = "200", description = "Conectividad OK - retorna pong")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
