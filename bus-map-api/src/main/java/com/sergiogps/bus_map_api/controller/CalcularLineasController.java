package com.sergiogps.bus_map_api.controller;

import com.sergiogps.bus_map_api.dto.RutaRequest;
import com.sergiogps.bus_map_api.dto.RutaResponse;
import com.sergiogps.bus_map_api.service.CalcularLineasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calcular-lineas")
@Tag(name = "Calculadora de Rutas", description = "Endpoints para calcular trayectos entre paradas") // Swagger
// @CrossOrigin(origins = "*") // Descomenta esto si tu app Android/Web te da error de CORS
public class CalcularLineasController {

    private static final Logger logger = LoggerFactory.getLogger(CalcularLineasController.class);
    private final CalcularLineasService calcularLineasService;

    public CalcularLineasController(CalcularLineasService calcularLineasService) {
        this.calcularLineasService = calcularLineasService;
    }

    /**
     * Endpoint principal para calcular rutas.
     * Recibe coordenadas de origen y destino.
     */
    @PostMapping("/ruta-avanzada")
    public ResponseEntity<RutaResponse> calcularRuta(@RequestBody RutaRequest request) {
        logger.info("Cálculo solicitado para: {}", request);
        RutaResponse response = calcularLineasService.calcularMejorOpcion(request.getLatOrigen(), request.getLonOrigen(), request.getLatDestino(), request.getLonDestino());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}