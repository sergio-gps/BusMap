package com.example.mimapa.util

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.mimapa.data.model.Parada
import com.example.mimapa.data.model.Waypoint
import com.google.android.gms.maps.model.LatLng

/**
 * Clase de utilidad para encontrar las ubicaciones más cercanas a un punto dado.
 *
 * @param context El contexto de la aplicación, necesario para leer ficheros de recursos.
 */
class NearestLocation(private val context: Context) {

    /**
     * Encuentra hasta 'maxResults' Waypoints más cercanos dentro de un rango especificado.
     *
     * @param myPosition Tu ubicación actual.
     * @param locations La lista de todos los Waypoints a comprobar.
     * @param rangeInMeters El rango de búsqueda en metros.
     * @param maxResults El número máximo de resultados a devolver.
     * @return Una lista de 0 a 'maxResults' Waypoints, ordenados por cercanía.
     */
    fun findClosestLocations(
        myPosition: LatLng,
        locations: List<Parada>,
        rangeInMeters: Float,
        maxResults: Int
    ): List<Parada> {

        val currentLocation = Location("CurrentLocation").apply {
            latitude = myPosition.latitude
            longitude = myPosition.longitude
        }

        // 1. Mapea cada Waypoint a un Par que contiene el Waypoint y su distancia
        return locations.map { parada ->
            val targetLocation = Location("TargetLocation").apply {
                latitude = parada.latitude
                longitude = parada.longitude
            }
            val distance = currentLocation.distanceTo(targetLocation)
            Pair(parada, distance)
        }
            // 2. Filtra solo los que están dentro del rango (ej. 500m)
            .filter { (_, distance) ->
                distance <= rangeInMeters
            }
            // 3. Ordena por distancia (el .second del Par) de menor a mayor
            .sortedBy { it.second }
            // 4. Toma los N primeros resultados
            .take(maxResults)
            // 5. Quita la distancia y devuelve solo la lista de Waypoints
            .map { it.first }
    }
    
    /**
     * Función de ejemplo para demostrar el uso de la clase.
     * En una app real, esta lógica estaría en un ViewModel o en un Composable.
     */
    fun main() {
        // --- Carga de datos ---
        // 1. Cargar las paradas desde el fichero /res/raw/paradas.json
        val todasLasParadas: List<Parada> = ParadasParser.parseParadas(context)

        // Posición de referencia del usuario
        val myPosition = LatLng(36.83814, -2.45974)
        
        // Ejecutar la búsqueda
        var closest = findClosestLocations(
            myPosition = myPosition,
            locations = todasLasParadas,
            rangeInMeters = 500f,
            maxResults = 3
        )
        if(closest.isEmpty()){
            closest = findClosestLocations(
                myPosition = myPosition,
                locations = todasLasParadas,
                rangeInMeters = 1000f,
                maxResults = 3)
        }

        // --- Resultado ---
        Log.d("NearestLocation", "Paradas más cercanas encontradas: ${closest.size}")
        closest.forEach { parada ->
            Log.d("NearestLocation", " - Coordenadas: ${parada.latitude}, ${parada.longitude}. Nombre: ${parada.nombre}")
        }
    }
}
