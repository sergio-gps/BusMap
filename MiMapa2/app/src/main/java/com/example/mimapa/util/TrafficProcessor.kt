package com.example.mimapa.util

import android.util.Log
import com.example.mimapa.data.model.RouteResponse
import com.example.mimapa.data.model.TrafficSegment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

/**
 * Utilidad para procesar rutas y extraer información de tráfico y combustible.
 */
data class ProcessedRoute(
    val trafficSegments: List<TrafficSegment>,
    val fuelConsumptionMicroliters: String? = null,
    val co2EmissionsKg: Double = 0.0,
    val distanceMeters: Int = 0,
    val duration: String = ""
)

object TrafficProcessor {

    /**
     * Extrae información completa de una respuesta de ruta.
     * Incluye segmentos de tráfico y consumo de combustible.
     */
    fun extractRouteInfo(response: RouteResponse?): ProcessedRoute {
        if (response == null || response.routes.isEmpty()) {
            return ProcessedRoute(emptyList())
        }

        val route = response.routes.first()
        val trafficSegments = extractTrafficSegments(response)
        
        // Buscar combustible en ambos niveles: primero en route, luego en legs
        val fuelConsumption = route.travelAdvisory?.fuelConsumptionMicroliters 
            ?: route.legs?.firstOrNull()?.travelAdvisory?.fuelConsumptionMicroliters
        
        val co2EmissionsKg = FuelConsumptionHelper.calculateDieselCo2Kg(fuelConsumption)
        val distance = route.distanceMeters
        val duration = route.duration

        return ProcessedRoute(
            trafficSegments = trafficSegments,
            fuelConsumptionMicroliters = fuelConsumption,
            co2EmissionsKg = co2EmissionsKg,
            distanceMeters = distance,
            duration = duration
        )
    }

    /**
     * Extrae los segmentos de tráfico de una respuesta de ruta.
     * Si hay información de tráfico, divide la polilínea según los intervalos.
     * Si no hay, devuelve un único segmento con la polilínea completa.
     *
     * @param response La respuesta de la API de Google Routes.
     * @return Lista de segmentos de tráfico con sus colores asociados.
     */
    fun extractTrafficSegments(response: RouteResponse?): List<TrafficSegment> {
        if (response == null || response.routes.isEmpty()) {
            return emptyList()
        }

        val route = response.routes.first()
        val encodedPolyline = route.polyline.encodedPolyline

        // Decodificar la polilínea
        val decodedPoints = try {
            PolyUtil.decode(encodedPolyline)
        } catch (e: Exception) {
            Log.e("TrafficProcessor", "Error al decodificar polilínea", e)
            return emptyList()
        }

        // Buscar información de tráfico en ambos niveles
        val speedIntervals = route.travelAdvisory?.speedReadingIntervals
            ?: route.legs?.firstOrNull()?.travelAdvisory?.speedReadingIntervals
            ?: emptyList()

        if (speedIntervals.isEmpty()) {
            return listOf(
                TrafficSegment(
                    points = decodedPoints,
                    speed = "NORMAL",
                    color = TrafficSegment.getColorBySpeed("NORMAL")
                )
            )
        }

        // Dividir la polilínea en segmentos según los intervalos de tráfico
        val segments = mutableListOf<TrafficSegment>()
        for (interval in speedIntervals) {
            val startIndex = interval.interval?.startIndex ?: 0
            val endIndex = interval.interval?.endIndex ?: decodedPoints.size - 1

            val segmentPoints = decodedPoints.subList(
                startIndex.coerceIn(0, decodedPoints.size - 1),
                (endIndex + 1).coerceIn(0, decodedPoints.size)
            )

            if (segmentPoints.isNotEmpty()) {
                segments.add(
                    TrafficSegment(
                        points = segmentPoints,
                        speed = interval.speed,
                        color = TrafficSegment.getColorBySpeed(interval.speed)
                    )
                )
            }
        }

        return if (segments.isEmpty()) {
            // Si algo falló, devolver la polilínea completa
            listOf(
                TrafficSegment(
                    points = decodedPoints,
                    speed = "NORMAL",
                    color = TrafficSegment.getColorBySpeed("NORMAL")
                )
            )
        } else {
            segments
        }
    }

    /**
     * Obtiene solo la polilínea decodificada sin información de tráfico.
     *
     * @param response La respuesta de la API de Google Routes.
     * @return Lista de coordenadas LatLng de la polilínea.
     */
    fun getDecodedPolyline(response: RouteResponse?): List<LatLng> {
        if (response == null || response.routes.isEmpty()) {
            return emptyList()
        }

        val encodedPolyline = response.routes.first().polyline.encodedPolyline
        return try {
            PolyUtil.decode(encodedPolyline)
        } catch (e: Exception) {
            Log.e("TrafficProcessor", "Error al decodificar polilínea", e)
            emptyList()
        }
    }
}
