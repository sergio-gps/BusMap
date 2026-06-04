package com.example.mimapa.ui.screens

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.data.model.TipoVehiculo
import com.example.mimapa.data.model.VehiculoAdmin
import com.example.mimapa.data.model.VehiculoAdminRequest
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.viewmodel.ManageVehiclesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVehiclesScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ManageVehiclesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var editingVehiculoId by remember { mutableIntStateOf(-1) }
    var selectedTipoId by remember { mutableStateOf<Int?>(null) }
    var nuevoTipoNombre by remember { mutableStateOf("") }
    var matriculaInput by remember { mutableStateOf("") }
    var marcaModeloInput by remember { mutableStateOf("") }
    var capacidadInput by remember { mutableStateOf("") }
    var activoInput by remember { mutableStateOf(true) }

    var tipoDropdownExpanded by remember { mutableStateOf(false) }
    var deleteDialogVehiculo by remember { mutableStateOf<VehiculoAdmin?>(null) }

    fun clearForm() {
        editingVehiculoId = -1
        selectedTipoId = null
        nuevoTipoNombre = ""
        matriculaInput = ""
        marcaModeloInput = ""
        capacidadInput = ""
        activoInput = true
        tipoDropdownExpanded = false
    }

    fun fillForm(vehiculo: VehiculoAdmin) {
        editingVehiculoId = vehiculo.vehiculoId ?: -1
        selectedTipoId = vehiculo.tipoId
        nuevoTipoNombre = ""
        matriculaInput = vehiculo.matricula.orEmpty()
        marcaModeloInput = vehiculo.marcaModelo.orEmpty()
        capacidadInput = vehiculo.capacidad?.toString().orEmpty()
        activoInput = vehiculo.activo ?: true
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData(context)
    }

    val selectedTipo = viewModel.tiposVehiculo.firstOrNull { it.tipoId == selectedTipoId }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Gestión de vehículos") },
                actions = {
                    IconButton(onClick = { navController.toSettings() }) {
                        Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Configuración")
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
                text = if (editingVehiculoId == -1) "Añadir vehículo" else "Modificar vehículo #$editingVehiculoId",
                style = MaterialTheme.typography.titleMedium.copy(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(0f, 1f),
                        blurRadius = 4f
                    )
                ),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = tipoDropdownExpanded,
                    onExpandedChange = { tipoDropdownExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    ManageVehiclesTextField(
                        value = selectedTipo?.nombre.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo existente") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = tipoDropdownExpanded,
                        onDismissRequest = { tipoDropdownExpanded = false }
                    ) {
                        viewModel.tiposVehiculo.forEach { tipo: TipoVehiculo ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(tipo.nombre) },
                                onClick = {
                                    selectedTipoId = tipo.tipoId
                                    tipoDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                ManageVehiclesTextField(
                    value = nuevoTipoNombre,
                    onValueChange = { nuevoTipoNombre = it },
                    label = { Text("Nuevo tipo") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            ManageVehiclesTextField(
                value = matriculaInput,
                onValueChange = { matriculaInput = it },
                label = { Text("Matrícula") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ManageVehiclesTextField(
                    value = marcaModeloInput,
                    onValueChange = { marcaModeloInput = it },
                    label = { Text("Marca / Modelo") },
                    singleLine = true,
                    modifier = Modifier.weight(1.7f)
                )
                ManageVehiclesTextField(
                    value = capacidadInput,
                    onValueChange = { capacidadInput = it },
                    label = { Text("Capacidad") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Activo")
                Switch(checked = activoInput, onCheckedChange = { activoInput = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val capacidad = capacidadInput.trim().toIntOrNull()
                        val tipoNombre = nuevoTipoNombre.trim().ifBlank { null }

                        if (matriculaInput.isBlank() || (selectedTipoId == null && tipoNombre == null)) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Debes indicar matrícula y un tipo existente o nuevo")
                            }
                            return@Button
                        }

                        val request = VehiculoAdminRequest(
                            vehiculoId = null,
                            tipoId = if (tipoNombre == null) selectedTipoId else null,
                            tipoNombre = tipoNombre,
                            matricula = matriculaInput.trim(),
                            marcaModelo = marcaModeloInput.trim().ifBlank { null },
                            capacidad = capacidad,
                            activo = activoInput
                        )

                        viewModel.createVehiculo(
                            request = request,
                            context = context,
                            onSuccess = {
                                clearForm()
                                scope.launch { snackbarHostState.showSnackbar("Vehículo creado correctamente") }
                            },
                            onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Añadir") }

                Button(
                    onClick = {
                        if (editingVehiculoId == -1) {
                            scope.launch { snackbarHostState.showSnackbar("Selecciona un vehículo para modificar") }
                            return@Button
                        }

                        val capacidad = capacidadInput.trim().toIntOrNull()
                        val tipoNombre = nuevoTipoNombre.trim().ifBlank { null }

                        if (matriculaInput.isBlank() || (selectedTipoId == null && tipoNombre == null)) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Debes indicar matrícula y un tipo existente o nuevo")
                            }
                            return@Button
                        }

                        val request = VehiculoAdminRequest(
                            vehiculoId = editingVehiculoId,
                            tipoId = if (tipoNombre == null) selectedTipoId else null,
                            tipoNombre = tipoNombre,
                            matricula = matriculaInput.trim(),
                            marcaModelo = marcaModeloInput.trim().ifBlank { null },
                            capacidad = capacidad,
                            activo = activoInput
                        )

                        viewModel.updateVehiculo(
                            vehiculoId = editingVehiculoId,
                            request = request,
                            context = context,
                            onSuccess = {
                                clearForm()
                                scope.launch { snackbarHostState.showSnackbar("Vehículo actualizado correctamente") }
                            },
                            onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Modificar") }

                Button(
                    onClick = { clearForm() },
                    modifier = Modifier.weight(1f)
                ) { Text("Limpiar") }
            }

            ManageVehiclesTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it, context) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por matrícula, id, marca/modelo o tipo") },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("", context) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true
            )

            Text(
                text = "Vehículos registrados",
                style = MaterialTheme.typography.titleMedium.copy(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(0f, 1f),
                        blurRadius = 4f
                    )
                ),
                fontWeight = FontWeight.Bold
            )

            if (viewModel.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.filteredVehiculos, key = { it.vehiculoId ?: -1 }) { vehiculo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "ID ${vehiculo.vehiculoId ?: "?"} | ${vehiculo.tipoNombre.orEmpty()}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Matrícula: ${vehiculo.matricula.orEmpty()} | Marca/Modelo: ${vehiculo.marcaModelo.orEmpty()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Capacidad: ${vehiculo.capacidad ?: 0} | Activo: ${if (vehiculo.activo == true) "Sí" else "No"}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { fillForm(vehiculo) }) {
                                        Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { deleteDialogVehiculo = vehiculo }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    deleteDialogVehiculo?.let { vehiculo ->
        AlertDialog(
            onDismissRequest = { deleteDialogVehiculo = null },
            title = { Text("Eliminar vehículo") },
            text = {
                Text(
                    "¿Eliminar vehículo ${vehiculo.vehiculoId} (${vehiculo.matricula.orEmpty()})?\n" +
                        "Se eliminará su info asociada y, si hubiera, el tipo de vehículo huérfano."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val vehiculoId = vehiculo.vehiculoId
                        if (vehiculoId != null) {
                            viewModel.deleteVehiculo(
                                vehiculoId = vehiculoId,
                                context = context,
                                onSuccess = {
                                    if (editingVehiculoId == vehiculoId) {
                                        clearForm()
                                    }
                                    deleteDialogVehiculo = null
                                    scope.launch { snackbarHostState.showSnackbar("Vehículo eliminado") }
                                },
                                onError = { error ->
                                    deleteDialogVehiculo = null
                                    scope.launch { snackbarHostState.showSnackbar(error) }
                                }
                            )
                        }
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogVehiculo = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ManageVehiclesTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = singleLine,
        readOnly = readOnly,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

