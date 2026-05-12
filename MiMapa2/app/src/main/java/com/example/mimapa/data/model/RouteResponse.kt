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
 * @param legs Lista de tramos que componen la ruta.
 */
@Serializable
data class Route(
    val distanceMeters: Int,
    val duration: String,
    val polyline: Polyline,
    val legs: List<RouteLeg>? = null,
    val travelAdvisory: TravelAdvisory? = null
)

/**
 * Representa la polilínea codificada de una ruta.
 */
@Serializable
data class Polyline(
    val encodedPolyline: String
)

/**
 * Representa un tramo de la ruta entre dos puntos.
 *
 * @param travelAdvisory Información sobre el tráfico y otras advisories del tramo.
 */
@Serializable
data class RouteLeg(
    val travelAdvisory: TravelAdvisory? = null
)

/**
 * Información sobre el tráfico y otras advisories de un tramo.
 *
 * @param speedReadingIntervals Lista de intervalos con información de velocidad/tráfico.
 * @param fuelConsumptionMicroliters Consumo de combustible en microlitros (divide entre 1,000,000 para litros).
 */
@Serializable
data class TravelAdvisory(
    val speedReadingIntervals: List<SpeedReadingInterval>? = null,
    val fuelConsumptionMicroliters: String? = null
)

/**
 * Representa un intervalo de la polilínea con información de tráfico.
 *
 * @param interval Índices de inicio y fin en la polilínea.
 * @param speed Densidad de tráfico: FAST, NORMAL, SLOW, CONGESTED.
 */
@Serializable
data class SpeedReadingInterval(
    val interval: PolylineInterval? = null,
    val speed: String = "NORMAL"
)

/**
 * Índices que definen un intervalo en la polilínea.
 *
 * @param startIndex Índice de inicio.
 * @param endIndex Índice de fin.
 */
@Serializable
data class PolylineInterval(
    val startIndex: Int = 0,
    val endIndex: Int = 0
)
