package com.sergiogps.bus_map_api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bus Map API",
                version = "1.0.0",
                description = "OpenAPI 3.0 definition for Bus Map API"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local"),
                @Server(url = "https://olympia.jpramez.dev", description = "Prod")
        }
)
public class OpenApiConfig {
}
