package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.data.model.TrafficSegment
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBar
import com.example.mimapa.ui.composables.RouteDurationCard
import com.example.mimapa.ui.composables.TrafficLegend
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawRouteScreen(navController: NavController) {

    var origin by remember { mutableStateOf<LatLng?>(null) }
    var destination by remember { mutableStateOf<LatLng?>(null) }
    var route by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var trafficSegments by remember { mutableStateOf<List<TrafficSegment>>(emptyList()) }
    var duration by remember { mutableStateOf("") }

    // --- ¡AQUÍ SE RECIBEN LOS DATOS! ---
    // LaunchedEffect se usa para leer los datos del SavedStateHandle una sola vez.
    LaunchedEffect(key1 = Unit) {
        navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
            origin = handle.get<LatLng>("origin")
            destination = handle.get<LatLng>("destination")
            route = handle.get<List<LatLng>>("route") ?: emptyList()
            trafficSegments = handle.get<List<TrafficSegment>>("trafficSegments") ?: emptyList()
            duration = handle.get<String>("duration") ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Ruta generada") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.toSettings() }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Solo muestra el mapa si tenemos un origen
            origin?.let {
                BusRoute(
                    originPosition = it,
                    destinationPosition = destination,
                    route = route,
                        trafficSegments = trafficSegments,
                        duration = duration
                )
            }
        }
    }
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun BusRoute(
    originPosition: LatLng,
    destinationPosition: LatLng?,
    route: List<LatLng>,
    trafficSegments: List<TrafficSegment> = emptyList(),
    duration: String = "",
    isPanelVisible: Boolean = true
) {

    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(originPosition, 15f)
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ) {
            // Marcador de Origen
            Marker(
                state = remember { MarkerState(position = originPosition) },
                title = "Origen"
            )

            // Marcador de Destino
            destinationPosition?.let {
                Marker(
                    state = remember { MarkerState(position = it) },
                    title = "Destino"
                )
            }

            // Dibuja segmentos de tráfico si están disponibles
            if (trafficSegments.isNotEmpty()) {
                trafficSegments.forEach { segment ->
                    if (segment.points.isNotEmpty()) {
                        Polyline(
                            points = segment.points,
                            color = segment.color,
                            width = 15f,
                            geodesic = true
                        )
                    }
                }
            } else if (route.isNotEmpty()) {
                // Si no hay segmentos de tráfico, dibuja la ruta completa
                Polyline(
                    points = route,
                    color = MaterialTheme.colorScheme.primary,
                    width = 15f
                )
            }
        }

        if (trafficSegments.isNotEmpty() || (isPanelVisible && duration.isNotEmpty())) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (trafficSegments.isNotEmpty()) {
                    TrafficLegend()
                }

                if (isPanelVisible && duration.isNotEmpty()) {
                    RouteDurationCard(
                        duration = formatDuration(duration)
                    )
                }
            }
        }
    }
}

private fun formatDuration(duration: String): String {
    val seconds = duration.replace("s", "").toLongOrNull() ?: return duration
    if (seconds < 60) return "$seconds seg"
    val minutes = seconds / 60
    if (minutes < 60) return "$minutes min"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
}
