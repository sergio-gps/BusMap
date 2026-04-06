package com.sergiogps.bus_map_api.dto;

/**
 * Respuesta enriquecida de vehículo para pantalla administrativa.
 */
public record VehiculoAdminDTO(
        Integer vehiculoId,
        Integer tipoId,
        String tipoNombre,
        String matricula,
        String marcaModelo,
        Integer capacidad,
        Boolean activo
) {
}
