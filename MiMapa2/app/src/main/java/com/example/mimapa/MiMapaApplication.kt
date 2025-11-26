package com.example.mimapa

import android.app.Application
import android.util.Log
import com.example.mimapa.BuildConfig.MAPS_API_KEY
import com.google.android.libraries.places.api.Places

class MiMapaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializePlacesSdk()
    }

    private fun initializePlacesSdk() {
        // Define a variable to hold the Places API key.
        val apiKey = MAPS_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("MiMapaApplication", "No se proporcionó la API key de Maps.")
            return
        }

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
    }
}
