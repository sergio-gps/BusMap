package com.example.mimapa.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.R
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.data.model.Parada
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBusStopsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val paradas = remember { mutableStateListOf<Parada>() }

    var numeroInput by remember { mutableStateOf("") }
    var nombreInput by remember { mutableStateOf("") }
    var latitudInput by remember { mutableStateOf("") }
    var longitudInput by remember { mutableStateOf("") }
    var lineasInput by remember { mutableStateOf("") }

    var paradaSeleccionadaId by remember { mutableIntStateOf(-1) }

    fun limpiarFormulario() {
        paradaSeleccionadaId = -1
        numeroInput = ""
        nombreInput = ""
        latitudInput = ""
        longitudInput = ""
        lineasInput = ""
    }

    fun cargarFormulario(parada: Parada) {
        paradaSeleccionadaId = parada.numero
        numeroInput = parada.numero.toString()
        nombreInput = parada.nombre
        latitudInput = parada.latitude.toString()
        longitudInput = parada.longitude.toString()
        lineasInput = parada.lineas.joinToString(",")
    }

    suspend fun recargarParadas() {
        try {
            val resultado = LlamadasAPI.getParadas(context)
            paradas.clear()
            paradas.addAll(resultado.sortedBy { it.numero })
        } catch (e: Exception) {
            Log.e("ManageBusStopsScreen", "No se pudo cargar el listado de paradas", e)
            snackbarHostState.showSnackbar("Error al cargar paradas desde el servidor")
        }
    }

    LaunchedEffect(Unit) {
        recargarParadas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Gestión de paradas")
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                BottomNavigationBarAdmin(navController = navController)
            }
        }
    ) { innerPadding ->
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Añadir o modificar parada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = numeroInput,
                onValueChange = { numeroInput = it },
                label = { Text("Número de parada") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nombreInput,
                onValueChange = { nombreInput = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitudInput,
                    onValueChange = { latitudInput = it },
                    label = { Text("Latitud") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = longitudInput,
                    onValueChange = { longitudInput = it },
                    label = { Text("Longitud") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = lineasInput,
                onValueChange = { lineasInput = it },
                label = { Text("Líneas (ej: 1,2,18)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val numero = numeroInput.trim().toIntOrNull()
                        val latitud = latitudInput.trim().toDoubleOrNull()
                        val longitud = longitudInput.trim().toDoubleOrNull()
                        val lineas = parseLineas(lineasInput)

                        if (numero == null || latitud == null || longitud == null || nombreInput.isBlank() || lineas.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa correctamente todos los campos")
                            }
                            return@Button
                        }

                        scope.launch {
                            val nuevaParada = Parada(
                                numero = numero,
                                nombre = nombreInput.trim(),
                                latitude = latitud,
                                longitude = longitud,
                                lineas = lineas
                            )

                            val ok = try {
                                LlamadasAPI.createParada(nuevaParada, context)
                            } catch (e: Exception) {
                                Log.e("ManageBusStopsScreen", "Error creando parada", e)
                                false
                            }

                            if (ok) {
                                snackbarHostState.showSnackbar("Parada creada correctamente")
                                limpiarFormulario()
                                recargarParadas()
                            } else {
                                snackbarHostState.showSnackbar("No se pudo crear la parada")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Añadir")
                }

                Button(
                    onClick = {
                        val numero = numeroInput.trim().toIntOrNull()
                        val latitud = latitudInput.trim().toDoubleOrNull()
                        val longitud = longitudInput.trim().toDoubleOrNull()
                        val lineas = parseLineas(lineasInput)

                        if (paradaSeleccionadaId == -1) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Selecciona una parada para actualizar")
                            }
                            return@Button
                        }

                        if (numero == null || latitud == null || longitud == null || nombreInput.isBlank() || lineas.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa correctamente todos los campos")
                            }
                            return@Button
                        }

                        scope.launch {
                            val paradaActualizada = Parada(
                                numero = numero,
                                nombre = nombreInput.trim(),
                                latitude = latitud,
                                longitude = longitud,
                                lineas = lineas
                            )

                            val ok = try {
                                LlamadasAPI.updateParada(paradaSeleccionadaId, paradaActualizada, context)
                            } catch (e: Exception) {
                                Log.e("ManageBusStopsScreen", "Error actualizando parada", e)
                                false
                            }

                            if (ok) {
                                snackbarHostState.showSnackbar("Parada actualizada correctamente")
                                limpiarFormulario()
                                recargarParadas()
                            } else {
                                snackbarHostState.showSnackbar("No se pudo actualizar la parada")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Modificar")
                }

                Button(
                    onClick = { limpiarFormulario() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpiar")
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val jsonParadas = context.resources
                            .openRawResource(R.raw.paradas)
                            .bufferedReader()
                            .use { it.readText() }

                        val ok = try {
                            LlamadasAPI.importParadasJson(jsonParadas, context)
                        } catch (e: Exception) {
                            Log.e("ManageBusStopsScreen", "Error importando paradas desde JSON", e)
                            false
                        }

                        if (ok) {
                            snackbarHostState.showSnackbar("Paradas importadas correctamente desde JSON")
                            recargarParadas()
                        } else {
                            snackbarHostState.showSnackbar("No se pudo importar el JSON de paradas")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cargar paradas a BBDD desde JSON")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Paradas registradas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paradas, key = { it.numero }) { parada ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${parada.numero} - ${parada.nombre}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lat/Lng: ${parada.latitude}, ${parada.longitude}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Líneas: ${parada.lineas.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { cargarFormulario(parada) }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Editar parada"
                                    )
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        val ok = try {
                                            LlamadasAPI.deleteParada(parada.numero, context)
                                        } catch (e: Exception) {
                                            Log.e("ManageBusStopsScreen", "Error borrando parada", e)
                                            false
                                        }

                                        if (ok) {
                                            snackbarHostState.showSnackbar("Parada eliminada")
                                            if (paradaSeleccionadaId == parada.numero) {
                                                limpiarFormulario()
                                            }
                                            recargarParadas()
                                        } else {
                                            snackbarHostState.showSnackbar("No se pudo eliminar la parada")
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Eliminar parada"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseLineas(lineasInput: String): List<Int> {
    return lineasInput
        .split(",")
        .mapNotNull { valor -> valor.trim().toIntOrNull() }
        .distinct()
}
