package com.example.mimapa.ui.composables

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val routePoints = listOf(
    LatLng(36.83936125117198, -2.459356635586987),
    LatLng(36.83992489533426, -2.4589854516073184),
    LatLng(36.84098746382203, -2.4582629274823584),
    LatLng(36.8411210374387, -2.4579178421303896),
    LatLng(36.84099287897318, -2.457597566836406),
    LatLng(36.84031278775575, -2.455978852865853),
    LatLng(36.8396887407288, -2.4559079661625223),
    LatLng(36.83886207319034, -2.456439616437503),
    LatLng(36.838294747160354, -2.456814303297966),
    LatLng(36.83822585728447, -2.4574978536515126),
    LatLng(36.83863514328335, -2.4585105208419527),
    LatLng(36.8390606363066, -2.459351034610018)
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun AutoAnimatedCyclicalMarkerMap() {
    val positionGps = LatLng(36.83814, -2.45974)
    // --- 2. Estado de la posición animada del marcador ---
    // Inicia en el primer punto de la lista
    val markerAnimatedPosition = remember { mutableStateOf(routePoints.first()) }

    // --- 3. Animador para la fracción (0f a 1f) ---
    val animationFraction = remember { Animatable(0f) }

    // --- 4. Efecto automático y cíclico ---
    LaunchedEffect(Unit) { // Se ejecuta una vez y se mantiene vivo

        val animationSpec = tween<Float>(durationMillis = 2000) // 2 seg. por tramo

        while (true) {

            // Itera por cada punto de la ruta
            for (i in routePoints.indices) {

                // Punto de inicio = posición actual del marcador
                val startPosition = markerAnimatedPosition.value

                // Punto de destino = el siguiente punto en la lista
                // Usamos el operador "módulo" (%) para que después del último (11),
                // vuelva al primero (0). (11 + 1) % 12 = 0.
                val targetPosition = routePoints[(i + 1) % routePoints.size]

                // Resetea la animación a 0f
                animationFraction.snapTo(0f)

                // Lanza la animación de este tramo
                launch {
                    animationFraction.animateTo(
                        targetValue = 1f,
                        animationSpec = animationSpec
                    ) {
                        // En cada frame, calcula la posición intermedia
                        markerAnimatedPosition.value = SphericalUtil.interpolate(
                            startPosition,
                            targetPosition,
                            value.toDouble() // 'value' es la fracción actual
                        )
                    }
                }

                // Espera a que la animación de 2 segundos termine
                delay(animationSpec.durationMillis.toLong())

                // (Opcional) Una pequeña pausa antes de empezar el siguiente tramo
                delay(500)
            }
        }
    }

    // --- 5. La UI del Mapa ---
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                // Centra la cámara en el primer punto
                position = CameraPosition.fromLatLngZoom(routePoints.first(), 17f) // Zoom más cercano
            }
        ) {
            Marker(
                state = remember { MarkerState(position = positionGps) },
                title = "One Marker"
            )
            // Marcador con la posición animada
            Marker(
                state = MarkerState(position = markerAnimatedPosition.value),
                title = "Vehículo en movimiento"
            )
        }
    }
}