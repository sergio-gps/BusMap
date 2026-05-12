package com.example.mimapa.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class MovimientoBuses(val linea: Int, val ruta: List<LatLngWrapper>)

@Serializable
data class LatLngWrapper(
    val lat: Double,
    val lng: Double
) {
    // Función de ayuda para convertir tu wrapper en el objeto real de Google Maps
    fun toGoogleMapsLatLng(): LatLng {
        return LatLng(lat, lng)
    }
}