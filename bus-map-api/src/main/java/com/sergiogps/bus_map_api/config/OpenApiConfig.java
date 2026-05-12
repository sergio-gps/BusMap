package com.sergiogps.bus_map_api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bus Map API",
                version = "1.0.0",
                description = "API REST para la gestión de transporte público con mapas interactivos. " +
                        "Proporciona funcionalidades de ubicación de vehículos, líneas de autobús, paradas, " +
                        "histórico de rutas y gestión de usuarios con roles y permisos.",
                contact = @Contact(
                        name = "Bus Map Team",
                        url = "https://github.com/SergiGPS/BusMap"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Ambiente de Desarrollo")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Bearer token para autenticación. Incluye el token en el header Authorization como: Bearer {token}"
)
public class OpenApiConfig {
}
