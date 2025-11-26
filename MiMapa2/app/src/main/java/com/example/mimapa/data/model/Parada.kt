package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

/**
 * Representa una parada de autobús del fichero paradas.json.
 *
 * @param numero El número identificador de la parada.
 * @param nombre El nombre de la parada.
 * @param latitude La coordenada de latitud.
 * @param longitude La coordenada de longitud.
 * @param lineas Lista de las líneas de autobús que pasan por esta parada.
 */
@Serializable
data class Parada(
    val numero: Int,
    val nombre: String,
    val latitude: Double,
    val longitude: Double,
    val lineas: List<Int>
)
