package com.example.mimapa.data.model

import android.location.Location

data class Waypoint(
    val location: Location,
    val via: Boolean? = null,
    val vehicleStopover: Boolean? = null,
    val sideOfRoad: Boolean? = null
)
