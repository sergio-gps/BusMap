package com.example.mimapa.util

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.mimapa.R
import com.example.mimapa.data.model.Parada
import com.example.mimapa.data.model.ParadasLinea
import com.example.mimapa.data.model.Waypoint
import com.google.android.gms.maps.model.LatLng

class FindBusLine(private val context: Context) {

    private val nearestLocation = NearestLocation(context)

    /**
     * Encuentra las líneas de autobús que conectan una ubicación de inicio y una de destino.
     */
    fun findCommonBusLines(
        startLocation: LatLng,
        destinationLocation: LatLng,
        allStops: List<Parada>,
        searchRadiusInMeters: Float = 500f
    ): Triple<List<Parada>, List<Parada>, List<Int>> {
        // 1. Encontrar paradas cercanas al punto de INICIO
        val nearbyStopsAtStart = nearestLocation.findClosestLocations(
            myPosition = startLocation,
            locations = allStops,
            rangeInMeters = searchRadiusInMeters,
            maxResults = 10
        )

        // 2. Encontrar paradas cercanas al punto de DESTINO
        val nearbyStopsAtDestination = nearestLocation.findClosestLocations(
            myPosition = destinationLocation,
            locations = allStops,
            rangeInMeters = searchRadiusInMeters,
            maxResults = 10
        )

        val linesNearStart = nearbyStopsAtStart.flatMap { it.lineas }.toSet()
        val linesNearDestination = nearbyStopsAtDestination.flatMap { it.lineas }.toSet()

        Log.d("FindBusLine", "Líneas cerca del inicio: $linesNearStart")
        Log.d("FindBusLine", "Líneas cerca del destino: $linesNearDestination")

        val commonLines = linesNearStart.intersect(linesNearDestination)
        Log.d("FindBusLine", "Líneas comunes encontradas: $commonLines")

        return Triple(nearbyStopsAtStart, nearbyStopsAtDestination, commonLines.toList())
    }

    /**
     * Determina el sentido correcto de una línea y devuelve las paradas de inicio/destino válidas.
     *
     * @param nearbyStopsAtStart Lista de paradas cercanas al origen.
     * @param nearbyStopsAtDestination Lista de paradas cercanas al destino.
     * @param paradasDeLaLineaIda Lista de paradas para el sentido de ida.
     * @param paradasDeLaLineaVuelta Lista de paradas para el sentido de vuelta.
     * @return Un Triple con (Parada de inicio, Parada de destino, Lista de ParadasLinea del sentido correcto), o null si no se encuentra un sentido válido.
     */
    fun identificarSentidoYParadas(
        nearbyStopsAtStart: List<Parada>,
        nearbyStopsAtDestination: List<Parada>,
        paradasDeLaLineaIda: List<ParadasLinea>,
        paradasDeLaLineaVuelta: List<ParadasLinea>
    ): Triple<Parada, Parada, List<ParadasLinea>>? {
        // Iterar sobre todas las combinaciones posibles de paradas de inicio y destino
        for (startStop in nearbyStopsAtStart) {
            for (destStop in nearbyStopsAtDestination) {

                // --- Comprobar sentido IDA ---
                val indiceInicialIda = paradasDeLaLineaIda.indexOfFirst { it.paradaId == startStop.numero }
                val indiceDestinoIda = paradasDeLaLineaIda.indexOfFirst { it.paradaId == destStop.numero }

                if (indiceInicialIda != -1 && indiceDestinoIda != -1 && indiceInicialIda < indiceDestinoIda) {
                    Log.d("FindBusLine", "Sentido IDA correcto encontrado: ${startStop.nombre} -> ${destStop.nombre}")
                    return Triple(startStop, destStop, paradasDeLaLineaIda)
                }

                // --- Comprobar sentido VUELTA ---
                val indiceInicialVuelta = paradasDeLaLineaVuelta.indexOfFirst { it.paradaId == startStop.numero }
                val indiceDestinoVuelta = paradasDeLaLineaVuelta.indexOfFirst { it.paradaId == destStop.numero }

                if (indiceInicialVuelta != -1 && indiceDestinoVuelta != -1 && indiceInicialVuelta < indiceDestinoVuelta) {
                    Log.d("FindBusLine", "Sentido VUELTA correcto encontrado: ${startStop.nombre} -> ${destStop.nombre}")
                    return Triple(startStop, destStop, paradasDeLaLineaVuelta)
                }
            }
        }

        Log.w("FindBusLine", "No se encontró un sentido válido para ninguna combinación de paradas.")
        return null // No se encontró un sentido válido
    }


    fun selectWaypointFromParadasLineas(paradaInicial: Parada, paradaDestino: Parada, paradasDeLaLinea: List<ParadasLinea>, allStops: List<Parada>): List<Waypoint> {
        val indiceInicial = paradasDeLaLinea.indexOfFirst { it.paradaId == paradaInicial.numero }
        val indiceDestino = paradasDeLaLinea.indexOfFirst { it.paradaId == paradaDestino.numero }

        if (indiceInicial == -1 || indiceDestino == -1 || indiceInicial >= indiceDestino) {
            Log.w("FindBusLine", "No se pudo encontrar el tramo de paradas o los índices son inválidos.")
            return emptyList()
        }

        val tramoDeParadas = paradasDeLaLinea.subList(indiceInicial, indiceDestino + 1)

        return tramoDeParadas.mapNotNull { paradaLinea ->
            val elemento = allStops.find { it.numero == paradaLinea.paradaId }
            elemento?.let {
                Waypoint(Location("TargetLocation").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                })
            }
        }
    }

    fun main() {
        val myPosition = LatLng(36.83814, -2.45974)
        val myDestination = LatLng(36.852869214705805, -2.4470123576287257)
        val allStops = ParadasParser.parseParadas(context)

        val (nearbyStopsAtStart, nearbyStopsAtDestination, commonLines) = findCommonBusLines(myPosition, myDestination, allStops)

        if (commonLines.isNotEmpty()) {
            val lineaId = 2 //commonLines.first() // Probamos con la primera línea común
            Log.d("FindBusLine", "Probando con la línea: $lineaId")

            val paradasDeLaLineaIda = ParadasLineaParser.parseParadasLinea(context, R.raw.paradas_linea_2_ida).filter { it.lineaId == lineaId }
            val paradasDeLaLineaVuelta = ParadasLineaParser.parseParadasLinea(context, R.raw.paradas_linea_2_vuelta).filter { it.lineaId == lineaId }

            val resultado = identificarSentidoYParadas(nearbyStopsAtStart, nearbyStopsAtDestination, paradasDeLaLineaIda, paradasDeLaLineaVuelta)

            if (resultado != null) {
                val (paradaInicioCorrecta, paradaDestinoCorrecta, sentidoCorrecto) = resultado
                Log.d("FindBusLine", "Parada de inicio final: ${paradaInicioCorrecta.nombre}")
                Log.d("FindBusLine", "Parada de destino final: ${paradaDestinoCorrecta.nombre}")

                val listaWaypoints = selectWaypointFromParadasLineas(paradaInicioCorrecta, paradaDestinoCorrecta, sentidoCorrecto, allStops)
                Log.d("FindBusLine", "Lista de waypoints a dibujar: ${listaWaypoints.size}")
            } else {
                Log.d("FindBusLine", "No se pudo determinar una ruta válida para la línea $lineaId.")
            }
        } else {
            Log.d("FindBusLine", "No se encontraron líneas comunes.")
        }
    }
}
