package com.sergiogps.bus_map_api.controller;

import com.sergiogps.bus_map_api.dto.HealthDto;
import com.sergiogps.bus_map_api.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    @Autowired
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Comprehensive health check endpoint
     * GET /api/health
     */
    @GetMapping
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
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
