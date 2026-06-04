package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.mimapa.util.GenerateRoute
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.AdminRoute.AdminDrawRoute.toAdminDrawRoute
import com.example.mimapa.Routes.MainRoute.DrawRoute.toDrawRoute
import com.example.mimapa.Routes.MainRoute.FindLocation.toFindLocation
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.viewmodel.GenerateAlternativeRouteViewModel
import com.example.mimapa.util.RouteParser
import com.example.mimapa.util.TrafficProcessor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateAlternativeRouteScreen(
    navController: NavController,
    vm: GenerateAlternativeRouteViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    var showDepartureDatePicker by remember { mutableStateOf(false) }
    var showDepartureTimePicker by remember { mutableStateOf(false) }
    val departureDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = vm.departureDateMillis ?: System.currentTimeMillis()
    )
    val departureTimePickerState = rememberTimePickerState(
        initialHour = vm.departureHour ?: 12,
        initialMinute = vm.departureMinute ?: 0,
        is24Hour = true
    )

    var showArrivalDatePicker by remember { mutableStateOf(false) }
    var showArrivalTimePicker by remember { mutableStateOf(false) }
    val arrivalDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = vm.arrivalDateMillis ?: System.currentTimeMillis()
    )
    val arrivalTimePickerState = rememberTimePickerState(
        initialHour = vm.arrivalHour ?: 12,
        initialMinute = vm.arrivalMinute ?: 0,
        is24Hour = true
    )

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

            // --- Checkboxes de Tiempos ---
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = vm.isDepartureTimeEnabled,
                    onCheckedChange = { vm.isDepartureTimeEnabled = it }
                )
                Text("Establecer momento de salida")
            }
            if (vm.isDepartureTimeEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (vm.departureDateMillis != null && vm.departureHour != null && vm.departureMinute != null) {
                            val fDate = Instant.ofEpochMilli(vm.departureDateMillis!!).atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            val fTime = String.format("%02d:%02d", vm.departureHour, vm.departureMinute)
                            "$fDate - $fTime"
                        } else "Seleccionar fecha y hora",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showDepartureDatePicker = true }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = vm.isArrivalTimeEnabled,
                    onCheckedChange = { vm.isArrivalTimeEnabled = it }
                )
                Text("Establecer momento de llegada")
            }
            if (vm.isArrivalTimeEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (vm.arrivalDateMillis != null && vm.arrivalHour != null && vm.arrivalMinute != null) {
                            val fDate = Instant.ofEpochMilli(vm.arrivalDateMillis!!).atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            val fTime = String.format("%02d:%02d", vm.arrivalHour, vm.arrivalMinute)
                            "$fDate - $fTime"
                        } else "Seleccionar fecha y hora",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showArrivalDatePicker = true }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

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

            // --- Dialogos Date/Time Picker Departure ---
            if (showDepartureDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDepartureDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.departureDateMillis = departureDatePickerState.selectedDateMillis
                            showDepartureDatePicker = false
                            showDepartureTimePicker = true // Encadenar con TimePicker
                        }) { Text("Siguiente") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDepartureDatePicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = departureDatePickerState)
                }
            }
            if (showDepartureTimePicker) {
                AlertDialog(
                    onDismissRequest = { showDepartureTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.departureHour = departureTimePickerState.hour
                            vm.departureMinute = departureTimePickerState.minute
                            showDepartureTimePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDepartureTimePicker = false }) { Text("Cancelar") }
                    },
                    text = { TimePicker(state = departureTimePickerState) }
                )
            }

            // --- Dialogos Date/Time Picker Arrival ---
            if (showArrivalDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showArrivalDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.arrivalDateMillis = arrivalDatePickerState.selectedDateMillis
                            showArrivalDatePicker = false
                            showArrivalTimePicker = true // Encadenar con TimePicker
                        }) { Text("Siguiente") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showArrivalDatePicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = arrivalDatePickerState)
                }
            }
            if (showArrivalTimePicker) {
                AlertDialog(
                    onDismissRequest = { showArrivalTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.arrivalHour = arrivalTimePickerState.hour
                            vm.arrivalMinute = arrivalTimePickerState.minute
                            showArrivalTimePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showArrivalTimePicker = false }) { Text("Cancelar") }
                    },
                    text = { TimePicker(state = arrivalTimePickerState) }
                )
            }

            // --- Botón final para buscar la ruta ---
            val isDepartureValid = !vm.isDepartureTimeEnabled || (vm.departureDateMillis != null && vm.departureHour != null && vm.departureMinute != null)
            val isArrivalValid = !vm.isArrivalTimeEnabled || (vm.arrivalDateMillis != null && vm.arrivalHour != null && vm.arrivalMinute != null)
            val isFormValid = vm.origin != null && vm.destination != null && isDepartureValid && isArrivalValid

            Button(
                onClick = {
                    scope.launch {
                        // Extraer los LatLng de los waypoints que ya tengan ubicación asignada
                        val intermediates = vm.waypointDisplayList
                            .filterNotNull()
                            .map { it.second }

                        val depTime = if (vm.isDepartureTimeEnabled) GenerateRoute.formatToRFC3339(vm.departureDateMillis!!, vm.departureHour!!, vm.departureMinute!!) else null
                        val arrTime = if (vm.isArrivalTimeEnabled) GenerateRoute.formatToRFC3339(vm.arrivalDateMillis!!, vm.arrivalHour!!, vm.arrivalMinute!!) else null

                        val googleMapResponse = try {
                            LlamadasAPI.computeRoute(
                                origin = vm.origin!!.second,
                                destination = vm.destination!!.second,
                                intermediates = intermediates,
                                departureTime = depTime,
                                arrivalTime = arrTime
                            )
                        } catch (e: Exception) {
                            Log.e("GenerateAlternativeRouteScreen", "Error al buscar la ruta: ${e.message}", e)
                            null
                        }
                        if (googleMapResponse != null) {
                            val parsedResponse = RouteParser.parseRouteResponse(googleMapResponse)
                            val processedRoute = TrafficProcessor.extractRouteInfo(parsedResponse)
                            val encodedPolyline = parsedResponse?.routes?.firstOrNull()?.polyline?.encodedPolyline
                            if (encodedPolyline != null) {
                                val rutaDecodificada = PolyUtil.decode(encodedPolyline)

                                // Guardamos los datos para la siguiente pantalla
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("origin", vm.origin!!.second)
                                    set("destination", vm.destination!!.second)
                                    set("route", rutaDecodificada)
                                    set("trafficSegments", processedRoute.trafficSegments)
                                    set("fuelConsumption", processedRoute.fuelConsumptionMicroliters)
                                    set("co2EmissionsKg", processedRoute.co2EmissionsKg)
                                    set("distanceMeters", processedRoute.distanceMeters)
                                    set("duration", processedRoute.duration)
                                }
                                navController.toAdminDrawRoute()
                            } else {
                                Log.e("GenerateAlternativeRouteScreen", "No se pudo obtener la polilínea codificada")
                            }
                        } else {
                            Log.e("GenerateAlternativeRouteScreen", "No se pudo obtener la ruta")
                        }
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Ruta")
            }
        }
    }
}