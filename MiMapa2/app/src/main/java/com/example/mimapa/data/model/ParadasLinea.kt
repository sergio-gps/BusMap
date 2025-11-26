package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ParadasLinea(
    val id: Int,
    val lineaId: Int,
    val paradaId: Int,
    val siguienteParada: Int
)