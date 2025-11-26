package com.example.mimapa.ui.screens

import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.MainRoute.DrawRoute.toDrawRoute
import com.example.mimapa.Routes.MainRoute.FindLocation.toFindLocation
import com.example.mimapa.data.model.Waypoint
import com.example.mimapa.repository.LocationRepository
import com.example.mimapa.util.RouteParser
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupSearchScreen(navController: NavController) {
    // --- 1. GESTIÓN DE ESTADOS ---
    // Almacena la información de origen y destino
    var destination by remember { mutableStateOf<Pair<String, LatLng>?>(null) }
    var origin by remember { mutableStateOf<Pair<String, LatLng>?>(null) }

    // Controla la UI: ¿usar ubicación actual? ¿estamos buscando origen o destino?
    var useCurrentLocation by remember { mutableStateOf<Boolean?>(null) }
    var isSelectingOrigin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationRepository = remember { LocationRepository(context) }

    val waypointList = listOf(
        Waypoint(
            location = Location("manual").apply {
                latitude = 36.85272110326484
                longitude = -2.4464245810739134
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.8485395192027
                longitude = -2.4467460616998924
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.846965624229995
                longitude = -2.4470708774258014
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.84496523325876
                longitude = -2.4474151125698507
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.84300937380353
                longitude = -2.4474345148584216
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.8413495069713
                longitude = -2.447495864438202
            }
        ), Waypoint(
            location = Location("manual").apply {
                latitude = 36.839687001879945
                longitude = -2.448644357319401
            }
        ))

    // --- 2. OBTENER RESULTADO DE FindDestinationScreen ---
    // Observamos el resultado que nos envía la pantalla de búsqueda.
    val navBackStackEntry = navController.currentBackStackEntry
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // --- ¡CORRECCIÓN APLICADA AQUÍ! ---
        val selectedLatLng = navBackStackEntry?.savedStateHandle?.get<LatLng>("selected_location")
        val selectedName = navBackStackEntry?.savedStateHandle?.get<String>("selected_location_name")

        if (selectedLatLng != null && selectedName != null) {
            val place = Pair(selectedName, selectedLatLng)
            if (isSelectingOrigin) {
                origin = place
            } else {
                destination = place
            }
            // Limpiamos el resultado para no volver a procesarlo
            navBackStackEntry.savedStateHandle.remove<LatLng>("selected_location")
            navBackStackEntry.savedStateHandle.remove<String>("selected_location_name")
        }
    }


    // --- 3. CONSTRUCCIÓN DE LA UI ---
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configurar Ruta") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Campo de Destino ---
            OutlinedTextField(
                value = destination?.first ?: "",
                onValueChange = {},
                label = { Text("Destino") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isSelectingOrigin = false
                        navController.toFindLocation()
                    },
                enabled = false, // Deshabilitado para forzar el click
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Transparent,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // --- Pregunta sobre ubicación actual ---
            Text("¿Quieres usar tu ubicación actual como origen?")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = {
                    useCurrentLocation = true
                    scope.launch {
                        try {
                            val currentLocation = locationRepository.getDeviceLocation().first()
                            origin = Pair("Mi ubicación actual", LatLng(currentLocation.latitude, currentLocation.longitude))
                        } catch (e: Exception) {
                            // Manejar error (e.g., permisos denegados, GPS desactivado)
                            origin = Pair("Error al obtener ubicación", LatLng(0.0, 0.0))
                            Log.e("Location Error", "Error al obtener ubicación actual",e)
                        }
                    }
                }) { Text("Sí") }

                Button(onClick = {
                    useCurrentLocation = false
                    origin = null // Limpiar por si se había seleccionado "Sí" antes
                }) { Text("No") }
            }

            // --- Campo de Origen (condicional) ---
            if (useCurrentLocation == false) {
                OutlinedTextField(
                    value = origin?.first ?: "",
                    onValueChange = {},
                    label = { Text("Origen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isSelectingOrigin = true
                            navController.toFindLocation()
                        },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Botón final para buscar la ruta ---
            Button(
                onClick = {
                    scope.launch {
                        val googleMapResponse = try {
                            LlamadasAPI.computeRoute(origin = origin!!.second, destination = destination!!.second, intermediates = waypointList.toLatLngList1(), context = context)
                        }catch (e: Exception){
                            Log.e("SetupSearchScreen", "Error al buscar la ruta: ${e.message}", e)
                            null
                        }
                        if (googleMapResponse != null) {
                            val parsedResponse = RouteParser.parseRouteResponse(googleMapResponse)
                            val encodedPolyline = parsedResponse?.routes?.firstOrNull()?.polyline?.encodedPolyline
                            if (encodedPolyline != null) {
                                val rutaDecodificada = PolyUtil.decode(encodedPolyline)

                                // Guardamos los datos para la siguiente pantalla
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("origin", origin!!.second)
                                    set("destination", destination!!.second)
                                    set("route", rutaDecodificada)
                                }
                                navController.toDrawRoute()
                            } else {
                                Log.e("SetupSearchScreen", "No se pudo obtener la polilínea codificada")
                            }
                        } else {
                            Log.e("SetupSearchScreen", "No se pudo obtener la ruta")
                        }
                    }
                },
                enabled = origin != null && destination != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Ruta")
            }
        }
    }
}

fun List<Waypoint>.toLatLngList1(): List<LatLng> = this.map { waypoint ->
    LatLng(waypoint.location.latitude, waypoint.location.longitude)
}
