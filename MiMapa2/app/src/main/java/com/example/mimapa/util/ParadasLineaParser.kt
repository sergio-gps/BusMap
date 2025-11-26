package com.example.mimapa.util

import android.content.Context
import android.util.Log
import com.example.mimapa.R
import com.example.mimapa.data.model.ParadasLinea
import kotlinx.serialization.json.Json
import java.io.InputStream

object ParadasLineaParser {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Lee el fichero paradas.json y devuelve una lista de objetos Parada.
     * Si ocurre un error, devuelve una lista vacía.
     * @param context Contexto de la aplicación
     * @return Lista de objetos Parada
     */
    fun parseParadasLinea(context: Context, id: Int): List<ParadasLinea> {
        return try {
            // 1. Abre el stream de datos desde el fichero en res/raw
            val inputStream: InputStream = context.resources.openRawResource(id)

            // 2. Lee el contenido del fichero
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // 3. Decodifica el String JSON a una lista de objetos Parada
            json.decodeFromString<List<ParadasLinea>>(jsonString)
        } catch (e: Exception) {
            Log.e("ParadasLineaParser", "Error al leer o parsear paradas_linea.json", e)
            // Devuelve una lista vacía en caso de error para evitar que la app falle
            emptyList()
        }
    }
}