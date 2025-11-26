package com.example.mimapa.util

import android.content.Context
import android.util.Log
import com.example.mimapa.R
import com.example.mimapa.data.model.Parada
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * Objeto de utilidad para parsear el fichero JSON de paradas de autobús.
 */
object ParadasParser {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Lee y parsea el fichero 'res/raw/paradas.json'.
     *
     * @param context El contexto de la aplicación para acceder a los recursos.
     * @return Una lista de objetos [Parada]. Devuelve una lista vacía si ocurre un error.
     */
    fun parseParadas(context: Context): List<Parada> {
        return try {
            // 1. Abre el stream de datos desde el fichero en res/raw
            val inputStream: InputStream = context.resources.openRawResource(R.raw.paradas)
            
            // 2. Lee el contenido del fichero
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            // 3. Decodifica el String JSON a una lista de objetos Parada
            json.decodeFromString<List<Parada>>(jsonString)
        } catch (e: Exception) {
            Log.e("ParadasParser", "Error al leer o parsear paradas.json", e)
            // Devuelve una lista vacía en caso de error para evitar que la app falle
            emptyList()
        }
    }
}
