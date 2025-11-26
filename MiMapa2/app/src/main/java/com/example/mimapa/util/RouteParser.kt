package com.example.mimapa.util

import android.util.Log
import com.example.mimapa.data.model.RouteResponse
import kotlinx.serialization.json.Json

/**
 * Un objeto de utilidad para parsear la respuesta JSON de la API de Google Routes.
 */
object RouteParser {

    // Se configura el parser de JSON.
    // ignoreUnknownKeys = true hace que la app no falle si Google añade nuevos campos al JSON.
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Convierte un String JSON en un objeto [RouteResponse].
     *
     * @param jsonString El JSON recibido de la API de Google Routes.
     * @return Un objeto [RouteResponse] si el parseo es exitoso, o null si ocurre un error.
     */
    fun parseRouteResponse(jsonString: String): RouteResponse? {
        return try {
            json.decodeFromString<RouteResponse>(jsonString)
        } catch (e: Exception) {
            Log.e("RouteParser", "Error al parsear la respuesta de la ruta", e)
            null
        }
    }
}
