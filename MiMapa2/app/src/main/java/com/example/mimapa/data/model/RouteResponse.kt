package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

/**
 * Representa la respuesta completa de la API de Google Routes.
 * Contiene una lista de posibles rutas.
 */
@Serializable
data class RouteResponse(
    val routes: List<Route>
)

/**
 * Representa una única ruta con sus detalles.
 *
 * @param distanceMeters La distancia total de la ruta en metros.
 * @param duration La duración estimada del viaje (ej. "602s").
 * @param polyline La polilínea codificada que representa el trazado de la ruta.
 */
@Serializable
data class Route(
    val distanceMeters: Int,
    val duration: String,
    val polyline: Polyline
)

/**
 * Representa la polilínea codificada de una ruta.
 */
@Serializable
data class Polyline(
    val encodedPolyline: String
)
