package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.mimapa.ui.composables.TrafficLegend
import com.example.mimapa.util.FuelConsumptionHelper
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
    var fuelConsumption by remember { mutableStateOf<String?>(null) }
    var co2EmissionsKg by remember { mutableStateOf<Double?>(null) }
    var distanceMeters by remember { mutableIntStateOf(0) }
    var duration by remember { mutableStateOf<String>("") }
    var isPanelVisible by remember { mutableStateOf(true) }

    // --- ¡AQUÍ SE RECIBEN LOS DATOS! ---
    // LaunchedEffect se usa para leer los datos del SavedStateHandle una sola vez.
    LaunchedEffect(key1 = Unit) {
        navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
            origin = handle.get<LatLng>("origin")
            destination = handle.get<LatLng>("destination")
            route = handle.get<List<LatLng>>("route") ?: emptyList()
            trafficSegments = handle.get<List<TrafficSegment>>("trafficSegments") ?: emptyList()
            fuelConsumption = handle.get<String>("fuelConsumption")
            co2EmissionsKg = handle.get<Double>("co2EmissionsKg")
            distanceMeters = handle.get<Int>("distanceMeters") ?: 0
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
        floatingActionButton = {
            FloatingActionButton(onClick = { isPanelVisible = !isPanelVisible }) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Mostrar/Ocultar Info panel"
                )
            }
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
                    fuelConsumption = fuelConsumption,
                    co2EmissionsKg = co2EmissionsKg,
                    distanceMeters = distanceMeters,
                    duration = duration,
                    isPanelVisible = isPanelVisible
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
    fuelConsumption: String? = null,
    co2EmissionsKg: Double? = null,
    distanceMeters: Int = 0,
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

        // Panel de información de la ruta
        if (isPanelVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                // Mostrar leyenda de tráfico si hay segmentos
                if (trafficSegments.isNotEmpty()) {
                    TrafficLegend()
                }

                // Mostrar información de ruta (Distancia y tiempo)
                if (distanceMeters > 0) {
                    RouteInfoCard(
                        distanceMeters = distanceMeters,
                        duration = duration
                    )
                }

                // Mostrar información de combustible si está disponible
                if (fuelConsumption != null && distanceMeters > 0) {
                    FuelInfoCard(
                        fuelConsumption = fuelConsumption,
                        co2EmissionsKg = co2EmissionsKg,
                        distanceMeters = distanceMeters
                    )
                }
            }
        }
    }
}

/**
 * Función auxiliar para formatear la duración de la API a texto legible
 */
private fun formatDuration(duration: String): String {
    val seconds = duration.replace("s", "").toLongOrNull() ?: return duration
    if (seconds < 60) return "$seconds seg"
    val minutes = seconds / 60
    if (minutes < 60) return "$minutes min"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
}

/**
 * Tarjeta que muestra información de la ruta: distancia y duración.
 */
@Composable
private fun RouteInfoCard(
    distanceMeters: Int,
    duration: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Información de viaje",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Distancia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Distancia:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "%.1f km".format(distanceMeters / 1000.0),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Duración
            if (duration.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tiempo estimado:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta que muestra información de combustible y CO2.
 */
@Composable
private fun FuelInfoCard(
    fuelConsumption: String?,
    co2EmissionsKg: Double?,
    distanceMeters: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Información ambiental",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Consumo de combustible
            if (fuelConsumption != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Consumo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FuelConsumptionHelper.formatFuelConsumption(fuelConsumption, distanceMeters),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (co2EmissionsKg != null && co2EmissionsKg > 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CO2 estimado:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FuelConsumptionHelper.formatDieselCo2Kg(co2EmissionsKg),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
