package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo de vehículo para gestión administrativa.
 */
@Serializable
data class VehiculoAdmin(
    val vehiculoId: Int? = null,
    val tipoId: Int? = null,
    val tipoNombre: String? = null,
    val matricula: String? = null,
    val marcaModelo: String? = null,
    val capacidad: Int? = null,
    val activo: Boolean? = null
)

/**
 * Payload de creación/edición de vehículo en backend.
 */
@Serializable
data class VehiculoAdminRequest(
    val vehiculoId: Int? = null,
    val tipoId: Int? = null,
    val tipoNombre: String? = null,
    val matricula: String,
    val marcaModelo: String? = null,
    val capacidad: Int? = null,
    val activo: Boolean = true
)

/**
 * Tipo de vehículo para selección en formularios.
 */
@Serializable
data class TipoVehiculo(
    val tipoId: Int,
    val nombre: String
)
