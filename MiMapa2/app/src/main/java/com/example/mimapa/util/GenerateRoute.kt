package com.example.mimapa.util

import android.location.Location
import com.example.mimapa.data.model.Waypoint
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GenerateRoute {

    companion object {
        /**
         * Formatea una fecha y hora al formato RFC 3339 (ISO 8601 en UTC) esperado por la API de Google Routes.
         */
        fun formatToRFC3339(dateMillis: Long, hour: Int, minute: Int): String {
            // El DatePicker nos da milisegundos en UTC, correspondientes a las 00:00:00 del día seleccionado
            // Convertimos a LocalDate en UTC, luego le sumamos la hora y aplicamos la zona horaria del dispositivo.
            val localDate = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
            val zonedDateTime = ZonedDateTime.of(localDate, java.time.LocalTime.of(hour, minute), ZoneId.systemDefault())
            
            // Formatear al estándar ISO_INSTANT (devuelve con sufijo 'Z')
            return DateTimeFormatter.ISO_INSTANT.format(zonedDateTime.toInstant())
        }
    }

    /**
     * Crea el cuerpo de la solicitud JSON para la API de Google Maps Routes.
     *
     * @param origin El Waypoint de origen.
     * @param destination El Waypoint de destino.
     * @param intermediates Lista de Waypoints intermedios (opcional).
     * @return Un JSONObject que representa el cuerpo de la solicitud.
     */
    fun createRoutesRequestBody(
        origin: Waypoint,
        destination: Waypoint,
        intermediates: List<Waypoint> = emptyList(),
        emissionType: String = "DIESEL",
        routingPreference: String = "TRAFFIC_AWARE_OPTIMAL",
        requestedReferenceRoutes: List<String> = emptyList(),
        departureTime: String? = null,
        arrivalTime: String? = null
    ): JSONObject {
        val originJson = waypointToJson(origin)
        val destinationJson = waypointToJson(destination)

        val body = JSONObject()
            .put("origin", originJson)
            .put("destination", destinationJson)
            .put("travelMode", "DRIVE")
            .put("routingPreference", routingPreference)
            .put("computeAlternativeRoutes", false)
            .put("extraComputations", JSONArray().put("FUEL_CONSUMPTION").put("TRAFFIC_ON_POLYLINE"))
            .put("routeModifiers", JSONObject().put("vehicleInfo", JSONObject().put("emissionType", emissionType)))

        departureTime?.let { body.put("departureTime", it) }
        arrivalTime?.let { body.put("arrivalTime", it) }

        if (requestedReferenceRoutes.isNotEmpty()) {
            body.put("requestedReferenceRoutes", JSONArray(requestedReferenceRoutes))
        }

        if (intermediates.isNotEmpty()) {
            val intermediatesJsonArray = JSONArray()
            intermediates.forEach { waypoint ->
                intermediatesJsonArray.put(waypointToJson(waypoint))
            }
            body.put("intermediates", intermediatesJsonArray)
        }

        return body
    }

    /**
     * Crea el cuerpo de la solicitud JSON para la API de Google Maps Routes usando LatLng.
     *
     * @param origin El punto de origen en coordenadas LatLng.
     * @param destination El punto de destino en coordenadas LatLng.
     * @param intermediates Lista de puntos intermedios en coordenadas LatLng (opcional).
     * @param emissionType Tipo de emisión: GASOLINE, DIESEL, ELECTRIC, HYBRID (por defecto DIESEL).
     * @return Un JSONObject que representa el cuerpo de la solicitud.
     */
    fun createRoutesRequestBody(
        origin: LatLng,
        destination: LatLng,
        intermediates: List<LatLng> = emptyList(),
        emissionType: String = "DIESEL",
        routingPreference: String = "TRAFFIC_AWARE_OPTIMAL",
        requestedReferenceRoutes: List<String> = emptyList(),
        departureTime: String? = null,
        arrivalTime: String? = null
    ): JSONObject {
        val originWaypoint = Waypoint(location = Location("manual").apply {
            latitude = origin.latitude
            longitude = origin.longitude
        })
        val destinationWaypoint = Waypoint(location = Location("manual").apply {
            latitude = destination.latitude
            longitude = destination.longitude
        })
        val intermediateWaypoints = intermediates.map { Waypoint(location = Location("manual").apply {
            latitude = it.latitude
            longitude = it.longitude
        }) }
        return createRoutesRequestBody(
            originWaypoint,
            destinationWaypoint,
            intermediateWaypoints,
            emissionType,
            routingPreference,
            requestedReferenceRoutes,
            departureTime,
            arrivalTime
        )
    }

    private fun waypointToJson(waypoint: Waypoint): JSONObject {
        val waypointJson = JSONObject()
        val locationJson = JSONObject().put(
            "latLng", JSONObject()
                .put("latitude", waypoint.location.latitude)
                .put("longitude", waypoint.location.longitude)
        )
        waypointJson.put("location", locationJson)

        waypoint.via?.let { waypointJson.put("via", it) }
        waypoint.vehicleStopover?.let { waypointJson.put("vehicleStopover", it) }
        waypoint.sideOfRoad?.let { waypointJson.put("sideOfRoad", it) }

        return waypointJson
    }
}
