package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

/**
 * Representa un rol de usuario en el sistema.
 *
 * @param rolId Identificador único del rol
 * @param rolName Nombre del rol (ej. "USER", "ADMIN")
 */
@Serializable
data class Rol(
    val rolId: Int,
    val rolName: String
)
