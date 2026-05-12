package com.example.mimapa.data.model

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

/**
 * Representa un segmento de la polilínea con su color asociado según el tráfico.
 *
 * @param points Lista de coordenadas LatLng que forman este segmento.
 * @param speed Densidad de tráfico: FAST, NORMAL, SLOW, CONGESTED.
 * @param color Color a utilizar para renderizar este segmento.
 */
data class TrafficSegment(
    val points: List<LatLng>,
    val speed: String,
    val color: Color
) {
    companion object {
        /**
         * Obtiene el color correspondiente según la densidad de tráfico.
         */
        fun getColorBySpeed(speed: String): Color = when (speed.uppercase()) {
            "NORMAL" -> Color(0xFF4CAF50) // Verde (tráfico fluido)
            "SLOW" -> Color(0xFFFF9800) // Naranja (tráfico lento)
            "TRAFFIC_JAM" -> Color(0xFFF44336) // Rojo (atasco)
            "CONGESTED" -> Color(0xFFF44336) // Rojo (por si acaso)
            else -> Color(0xFF9E9E9E) // Gris
        }

        /**
         * Obtiene una descripción legible del estado de tráfico.
         */
        fun getSpeedDescription(speed: String): String = when (speed.uppercase()) {
            "NORMAL" -> "Tráfico fluido"
            "SLOW" -> "Tráfico lento"
            "TRAFFIC_JAM", "CONGESTED" -> "Atasco"
            else -> "Desconocido"
        }
    }
}
