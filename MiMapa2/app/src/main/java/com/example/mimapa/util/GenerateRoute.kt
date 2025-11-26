package com.example.mimapa.util

import android.location.Location
import com.example.mimapa.data.model.Waypoint
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class GenerateRoute {

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
        intermediates: List<Waypoint> = emptyList()
    ): JSONObject {
        val originJson = waypointToJson(origin)
        val destinationJson = waypointToJson(destination)

        val body = JSONObject()
            .put("origin", originJson)
            .put("destination", destinationJson)
            .put("travelMode", "DRIVE")
            .put("routingPreference", "TRAFFIC_AWARE")
            .put("computeAlternativeRoutes", false)
            .put("languageCode", "es-ES")
            .put("units", "METRIC")

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
     * Función de conveniencia para crear el cuerpo de la solicitud a partir de LatLng.
     */
    fun createRoutesRequestBody(
        origin: LatLng,
        destination: LatLng,
        intermediates: List<LatLng> = emptyList()
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
        return createRoutesRequestBody(originWaypoint, destinationWaypoint, intermediateWaypoints)
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
