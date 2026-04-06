package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

/**
 * Representa una línea de autobús en la app.
 */
@Serializable
data class Linea(
    val lineaId: Int,
    val nombre: String,
    val origen: String? = null,
    val destino: String? = null,
    val color: String? = null,
    val paradas: List<ParadaLinea> = emptyList()
)

/**
 * Representa una parada dentro del contexto de una línea.
 */
@Serializable
data class ParadaLinea(
    val paradaId: Int,
    val nombre: String? = null
)
