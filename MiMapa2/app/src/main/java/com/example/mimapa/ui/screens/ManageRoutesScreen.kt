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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.data.model.Linea
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.viewmodel.ManageRoutesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRoutesScreen(navController: NavController) {
	val context = LocalContext.current
	val viewModel: ManageRoutesViewModel = viewModel()
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	var lineaIdInput by remember { mutableStateOf("") }
	var nombreInput by remember { mutableStateOf("") }
	var origenInput by remember { mutableStateOf("") }
	var destinoInput by remember { mutableStateOf("") }
	var colorInput by remember { mutableStateOf("") }
	var editingLineaId by remember { mutableIntStateOf(-1) }

	var selectedLineaId by remember { mutableIntStateOf(-1) }
	var paradaSearchQuery by remember { mutableStateOf("") }
	var paradaDropdownExpanded by remember { mutableStateOf(false) }

	fun clearForm() {
		lineaIdInput = ""
		nombreInput = ""
		origenInput = ""
		destinoInput = ""
		colorInput = ""
		editingLineaId = -1
	}

	fun fillForm(linea: Linea) {
		lineaIdInput = linea.lineaId.toString()
		nombreInput = linea.nombre
		origenInput = linea.origen.orEmpty()
		destinoInput = linea.destino.orEmpty()
		colorInput = linea.color.orEmpty()
		editingLineaId = linea.lineaId
	}

	LaunchedEffect(Unit) {
		viewModel.loadInitialData(context)
	}

	val selectedLinea = viewModel.lineasList.firstOrNull { it.lineaId == selectedLineaId }
	val assignedParadaIds = selectedLinea?.paradas?.map { it.paradaId }?.toSet() ?: emptySet()
	val availableParadas = viewModel.paradasList
		.filter { parada ->
			!assignedParadaIds.contains(parada.numero) &&
				(paradaSearchQuery.isBlank() ||
					parada.nombre.contains(paradaSearchQuery, ignoreCase = true) ||
					parada.numero.toString().contains(paradaSearchQuery))
		}
		.take(20)

	Scaffold(
		topBar = {
			TopAppBar(
				colors = topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					titleContentColor = MaterialTheme.colorScheme.primary,
				),
				title = { Text("Gestión de líneas") },
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
				text = "Añadir o modificar línea",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ManageRoutesTextField(
					value = lineaIdInput,
					label = "ID",
					onValueChange = { lineaIdInput = it },
					modifier = Modifier.weight(1f)
				)
				ManageRoutesTextField(
					value = nombreInput,
					label = "Nombre",
					onValueChange = { nombreInput = it },
					modifier = Modifier.weight(2f)
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ManageRoutesTextField(
					value = origenInput,
					label = "Origen",
					onValueChange = { origenInput = it },
					modifier = Modifier.weight(1f)
				)
				ManageRoutesTextField(
					value = destinoInput,
					label = "Destino",
					onValueChange = { destinoInput = it },
					modifier = Modifier.weight(1f)
				)
			}

			ManageRoutesTextField(
				value = colorInput,
				label = "Color",
				onValueChange = { colorInput = it },
				modifier = Modifier.fillMaxWidth()
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Button(
					onClick = {
						val lineaId = lineaIdInput.trim().toIntOrNull()
						if (lineaId == null || nombreInput.isBlank()) {
							scope.launch { snackbarHostState.showSnackbar("ID y nombre son obligatorios") }
							return@Button
						}

						val nuevaLinea = Linea(
							lineaId = lineaId,
							nombre = nombreInput.trim(),
							origen = origenInput.trim().ifBlank { null },
							destino = destinoInput.trim().ifBlank { null },
							color = colorInput.trim().ifBlank { null }
						)

						viewModel.createLinea(
							linea = nuevaLinea,
							context = context,
							onSuccess = {
								clearForm()
								scope.launch { snackbarHostState.showSnackbar("Línea creada correctamente") }
							},
							onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
						)
					},
					modifier = Modifier.weight(1f)
				) {
					Text("Añadir")
				}

				Button(
					onClick = {
						if (editingLineaId == -1) {
							scope.launch { snackbarHostState.showSnackbar("Selecciona una línea para modificar") }
							return@Button
						}

						val lineaId = lineaIdInput.trim().toIntOrNull()
						if (lineaId == null || nombreInput.isBlank()) {
							scope.launch { snackbarHostState.showSnackbar("ID y nombre son obligatorios") }
							return@Button
						}

						val lineaActualizada = Linea(
							lineaId = lineaId,
							nombre = nombreInput.trim(),
							origen = origenInput.trim().ifBlank { null },
							destino = destinoInput.trim().ifBlank { null },
							color = colorInput.trim().ifBlank { null }
						)

						viewModel.updateLinea(
							lineaId = editingLineaId,
							linea = lineaActualizada,
							context = context,
							onSuccess = {
								clearForm()
								scope.launch { snackbarHostState.showSnackbar("Línea actualizada correctamente") }
							},
							onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
						)
					},
					modifier = Modifier.weight(1f)
				) {
					Text("Modificar")
				}

				Button(
					onClick = { clearForm() },
					modifier = Modifier.weight(1f)
				) {
					Text("Limpiar")
				}
			}

			ManageRoutesTextField(
				value = viewModel.searchQuery,
				label = "Buscar línea por nombre o ID",
				onValueChange = { viewModel.updateSearchQuery(it) },
				modifier = Modifier.fillMaxWidth()
			)

			Text(
				text = "Líneas registradas",
				style = MaterialTheme.typography.titleMedium,
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
					items(viewModel.filteredLineas, key = { it.lineaId }) { linea ->
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
									text = "${linea.lineaId} - ${linea.nombre}",
									style = MaterialTheme.typography.titleSmall,
									fontWeight = FontWeight.SemiBold
								)
								Text(
									text = "Origen: ${linea.origen.orEmpty()} | Destino: ${linea.destino.orEmpty()}",
									style = MaterialTheme.typography.bodySmall
								)
								Text(
									text = "Color: ${linea.color.orEmpty()} | Paradas: ${linea.paradas.size}",
									style = MaterialTheme.typography.bodySmall
								)

								Row(
									modifier = Modifier.fillMaxWidth(),
									horizontalArrangement = Arrangement.End
								) {
									Button(
										onClick = {
											selectedLineaId = linea.lineaId
											paradaSearchQuery = ""
											paradaDropdownExpanded = false
										}
									) {
										Text("Paradas")
									}

									IconButton(onClick = { fillForm(linea) }) {
										Icon(
											imageVector = Icons.Rounded.Edit,
											contentDescription = "Editar línea"
										)
									}

									IconButton(onClick = {
										viewModel.deleteLinea(
											lineaId = linea.lineaId,
											context = context,
											onSuccess = {
												if (selectedLineaId == linea.lineaId) {
													selectedLineaId = -1
												}
												scope.launch { snackbarHostState.showSnackbar("Línea eliminada") }
											},
											onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
										)
									}) {
										Icon(
											imageVector = Icons.Rounded.Delete,
											contentDescription = "Eliminar línea",
											tint = MaterialTheme.colorScheme.error
										)
									}
								}
							}
						}
					}
				}
			}

			if (selectedLinea != null) {
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = "Gestionar paradas de línea ${selectedLinea.lineaId}",
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Bold
				)

				ExposedDropdownMenuBox(
					expanded = paradaDropdownExpanded,
					onExpandedChange = { paradaDropdownExpanded = it }
				) {
					ManageRoutesTextField(
						value = paradaSearchQuery,
						label = "Buscar parada para añadir",
						onValueChange = {
							paradaSearchQuery = it
							paradaDropdownExpanded = true
						},
						modifier = Modifier
							.menuAnchor()
							.fillMaxWidth(),
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paradaDropdownExpanded) }
					)

					ExposedDropdownMenu(
						expanded = paradaDropdownExpanded,
						onDismissRequest = { paradaDropdownExpanded = false }
					) {
						availableParadas.forEach { parada ->
							androidx.compose.material3.DropdownMenuItem(
								text = { Text("${parada.numero} - ${parada.nombre}") },
								onClick = {
									viewModel.addParadaToLinea(
										lineaId = selectedLinea.lineaId,
										paradaId = parada.numero,
										context = context,
										onSuccess = {
											paradaSearchQuery = ""
											paradaDropdownExpanded = false
											scope.launch { snackbarHostState.showSnackbar("Parada añadida") }
										},
										onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
									)
								}
							)
						}
					}
				}

				LazyColumn(
					modifier = Modifier
						.fillMaxWidth()
						.height(160.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					items(selectedLinea.paradas, key = { it.paradaId }) { parada ->
						Card(
							modifier = Modifier.fillMaxWidth(),
							colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
						) {
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(horizontal = 10.dp, vertical = 8.dp),
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = "${parada.paradaId} - ${parada.nombre.orEmpty()}",
									style = MaterialTheme.typography.bodySmall
								)
								IconButton(onClick = {
									viewModel.removeParadaFromLinea(
										lineaId = selectedLinea.lineaId,
										paradaId = parada.paradaId,
										context = context,
										onSuccess = { scope.launch { snackbarHostState.showSnackbar("Parada quitada") } },
										onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
									)
								}) {
									Icon(
										imageVector = Icons.Rounded.Delete,
										contentDescription = "Quitar parada",
										tint = MaterialTheme.colorScheme.error
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

@Composable
fun ManageRoutesTextField(
	value: String,
	label: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	singleLine: Boolean = true,
	trailingIcon: @Composable (() -> Unit)? = null
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		singleLine = singleLine,
		modifier = modifier,
		trailingIcon = trailingIcon,
		colors = OutlinedTextFieldDefaults.colors(
			focusedContainerColor = MaterialTheme.colorScheme.surface,
			unfocusedContainerColor = MaterialTheme.colorScheme.surface
		)
	)
}

