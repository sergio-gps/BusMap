package com.example.mimapa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mimapa.ui.screens.MapScreen
import com.example.mimapa.ui.theme.MiMapaTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Thread.sleep

class EjemploGoogleMaps : ComponentActivity() {

    private val client = OkHttpClient()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                MiMapaTheme {
                    var apiResponse by remember { mutableStateOf("Cargando...") }

                    LaunchedEffect(Unit) {
                        run { result ->
                            apiResponse = result
                        }
                    }
                    ProyectoPrincipal(apiResponse = apiResponse)
                }
            }
        }

        fun run(onResult: (String) -> Unit) {
            sleep(3000)
            val request = Request.Builder()
                .url("http://olympia.jpramez.dev:8080/api/health/status")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onResult("Error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            onResult("Error en respuesta: $response")
                        } else {
                            val body = response.body.string()
                            onResult(body)
                        }
                    }
                }
            })
        }
    }

    @Composable
    fun ProyectoPrincipal(modifier: Modifier = Modifier, apiResponse: String) {

        Column {
            PimeraParte(modifier)
            MapScreen()
        }
    }

    @Composable
    fun PimeraParte(modifier: Modifier = Modifier) {
        Card(modifier = modifier.fillMaxWidth()) {

            Text(
                modifier = modifier.padding(20.dp),
                text = "Primera Parte"
            )
        }
    }

    @Composable
    fun SegundaParte(modifier: Modifier = Modifier, apiResponse: String) {
        Text(
            text = apiResponse
        )
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun MapScreenEjemplo() {
        val atasehir = LatLng(40.9971, 29.1007)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(atasehir, 15f)
        }

        var uiSettings by remember {
            mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
        }
        var properties by remember {
            mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
        }

        GoogleMap(
            modifier = Modifier,
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ) {
            Marker(
                state = MarkerState(position = atasehir),
                title = "One Marker"
            )
        }
    }

    @Preview
    @Composable
    fun ProyectoPrincipalPreview(modifier: Modifier = Modifier) {
        ProyectoPrincipal(apiResponse = "EJEMPLO")
    }