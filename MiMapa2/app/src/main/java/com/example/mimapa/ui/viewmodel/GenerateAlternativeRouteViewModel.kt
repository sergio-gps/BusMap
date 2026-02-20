package com.example.mimapa.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

/**
 * ViewModel para la pantalla de generación de ruta alternativa.
 */
class GenerateAlternativeRouteViewModel : ViewModel() {

    var origin by mutableStateOf<Pair<String, LatLng>?>(null)
    var destination by mutableStateOf<Pair<String, LatLng>?>(null)
    var isSelectingOrigin by mutableStateOf(true)

    // -1 = ningún waypoint en edición; >=0 = índice del que se está editando
    var editingWaypointIndex by mutableStateOf(-1)

    // Lista observable de waypoints para la UI (nombre + coordenadas)
    val waypointDisplayList = mutableStateListOf<Pair<String, LatLng>?>()

    fun addWaypoint() {
        waypointDisplayList.add(null)
    }

    fun setWaypoint(index: Int, place: Pair<String, LatLng>) {
        waypointDisplayList[index] = place
    }
}

