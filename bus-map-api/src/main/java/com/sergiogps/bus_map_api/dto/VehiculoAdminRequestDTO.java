package com.sergiogps.bus_map_api.dto;

/**
 * Payload de alta/edición de vehículos para la gestión administrativa.
 */
public record VehiculoAdminRequestDTO(
        Integer vehiculoId,
        Integer tipoId,
        String tipoNombre,
        String matricula,
        String marcaModelo,
        Integer capacidad,
        Boolean activo
) {
}
