package com.example.mimapa.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.MainRoute.DrawRoute.toDrawRoute
import com.example.mimapa.Routes.MainRoute.FindLocation.toFindLocation
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.viewmodel.GenerateAlternativeRouteViewModel
import com.example.mimapa.util.RouteParser
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateAlternativeRouteScreen(
    navController: NavController,
    vm: GenerateAlternativeRouteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- OBTENER RESULTADO DE FindLocationScreen ---
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val savedHandle = navController.currentBackStackEntry?.savedStateHandle
        val selectedLatLng = savedHandle?.get<LatLng>("selected_location")
        val selectedName   = savedHandle?.get<String>("selected_location_name")

        if (selectedLatLng != null && selectedName != null) {
            val place = Pair(selectedName, selectedLatLng)
            when {
                vm.editingWaypointIndex >= 0 -> {
                    vm.setWaypoint(vm.editingWaypointIndex, place)
                    vm.editingWaypointIndex = -1
                }
                vm.isSelectingOrigin -> vm.origin = place
                else -> vm.destination = place
            }
            savedHandle.remove<LatLng>("selected_location")
            savedHandle.remove<String>("selected_location_name")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Generar ruta alternativa") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                BottomNavigationBarAdmin(navController = navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- Campo Origen ---
            OutlinedTextField(
                value = vm.origin?.first ?: "",
                onValueChange = {},
                label = { Text("Origen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        vm.isSelectingOrigin = true
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

            // --- Campos de Waypoints dinámicos ---
            vm.waypointDisplayList.forEachIndexed { index, waypoint ->
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = waypoint?.first ?: "",
                    onValueChange = {},
                    label = { Text("Waypoint ${index + 1}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.editingWaypointIndex = index
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

            // --- Botón Añadir Waypoint ---
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { vm.addWaypoint() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Añadir Waypoint")
            }

            // --- Campo Destino ---
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = vm.destination?.first ?: "",
                onValueChange = {},
                label = { Text("Destino") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        vm.isSelectingOrigin = false
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

            Spacer(modifier = Modifier.weight(1f))

            // --- Botón final para buscar la ruta ---
            Button(
                onClick = {
                    scope.launch {
                        // Extraer los LatLng de los waypoints que ya tengan ubicación asignada
                        val intermediates = vm.waypointDisplayList
                            .filterNotNull()
                            .map { it.second }

                        val googleMapResponse = try {
                            LlamadasAPI.computeRoute(
                                origin = vm.origin!!.second,
                                destination = vm.destination!!.second,
                                intermediates = intermediates,
                                context = context
                            )
                        } catch (e: Exception) {
                            Log.e("GenerateAlternativeRouteScreen", "Error al buscar la ruta: ${e.message}", e)
                            null
                        }
                        if (googleMapResponse != null) {
                            val parsedResponse = RouteParser.parseRouteResponse(googleMapResponse)
                            val encodedPolyline = parsedResponse?.routes?.firstOrNull()?.polyline?.encodedPolyline
                            if (encodedPolyline != null) {
                                val rutaDecodificada = PolyUtil.decode(encodedPolyline)

                                // Guardamos los datos para la siguiente pantalla
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("origin", vm.origin!!.second)
                                    set("destination", vm.destination!!.second)
                                    set("route", rutaDecodificada)
                                }
                                navController.toDrawRoute()
                            } else {
                                Log.e("GenerateAlternativeRouteScreen", "No se pudo obtener la polilínea codificada")
                            }
                        } else {
                            Log.e("GenerateAlternativeRouteScreen", "No se pudo obtener la ruta")
                        }
                    }
                },
                enabled = vm.origin != null && vm.destination != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Ruta")
            }
        }
    }
}