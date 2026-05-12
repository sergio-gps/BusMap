package com.example.mimapa.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.data.model.Rol
import com.example.mimapa.data.model.Usuario
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.viewmodel.ManageUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ManageUsersViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Diálogos
    val deleteConfirmationDialog = remember { mutableStateOf<Usuario?>(null) }
    val editingUsuario = remember { mutableStateOf<Usuario?>(null) }
    val editingUsername = remember { mutableStateOf("") }
    val editingEmail = remember { mutableStateOf("") }
    val editingIsAdmin = remember { mutableStateOf(false) }

    // Cargar usuarios al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadUsuarios(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Gestión de usuarios")
                },
                actions = {
                    IconButton(onClick = {
                        navController.toSettings()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                })
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                BottomNavigationBarAdmin(navController = navController)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre o email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.updateSearchQuery("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mostrar error si existe
            if (viewModel.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewModel.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Mostrar carga o lista de usuarios
            if (viewModel.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.filteredUsuarios.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (viewModel.searchQuery.isNotEmpty()) 
                            "No se encontraron usuarios" 
                        else 
                            "No hay usuarios disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Cabecera de la lista
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Usuario",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Email",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Acciones",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Lista de usuarios
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.filteredUsuarios) { usuario ->
                        UserRowCard(
                            usuario = usuario,
                            onEdit = {
                                editingUsuario.value = usuario
                                editingUsername.value = usuario.username ?: ""
                                editingEmail.value = usuario.email
                                editingIsAdmin.value = usuario.roles.any { it.rolName == "ADMIN" || it.rolId == 2 }
                            },
                            onDelete = {
                                deleteConfirmationDialog.value = usuario
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    deleteConfirmationDialog.value?.let { usuario ->
        AlertDialog(
            onDismissRequest = { deleteConfirmationDialog.value = null },
            title = { Text("Eliminar usuario") },
            text = {
                Text("¿Está seguro de que desea eliminar a ${usuario.username ?: usuario.email}?\n\nEsta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUsuario(
                            usuario.usuarioId,
                            context,
                            onSuccess = {
                                deleteConfirmationDialog.value = null
                            },
                            onError = { error ->
                                // El error se mostrará en el estado del ViewModel
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmationDialog.value = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de edición de usuario
    editingUsuario.value?.let { usuario ->
        AlertDialog(
            onDismissRequest = { editingUsuario.value = null },
            title = { Text("Editar usuario") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editingUsername.value,
                        onValueChange = { editingUsername.value = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                    OutlinedTextField(
                        value = editingEmail.value,
                        onValueChange = { editingEmail.value = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = editingIsAdmin.value,
                            onCheckedChange = { editingIsAdmin.value = it }
                        )
                        Text("Dar rol de Admin", modifier = Modifier.padding(start = 8.dp))
                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Construir la lista de roles: mantener los no-Admin y agregar/quitar Admin
                        val rolesList = usuario.roles.filter { it.rolName != "ADMIN" && it.rolId != 2 }.toMutableList()

                        // Si está marcado como Admin, agregar el rol Admin
                        if (editingIsAdmin.value) {
                            // Obtener el Admin actual del usuario para usar su rolId correcto
                            val adminRol = usuario.roles.find { it.rolName == "ADMIN" || it.rolId == 2 }
                            if (adminRol != null) {
                                rolesList.add(adminRol)
                            } else {
                                // Si no existe Admin previo, crear uno con el ID correcto
                                rolesList.add(Rol(rolId = 2, rolName = "ADMIN"))
                            }
                        }

                        val updatedUsuario = usuario.copy(
                            username = editingUsername.value.ifBlank { null },
                            email = editingEmail.value,
                            roles = rolesList
                        )
                        viewModel.updateUsuario(
                            usuario.usuarioId,
                            updatedUsuario,
                            context,
                            onSuccess = {
                                editingUsuario.value = null
                            },
                            onError = { error ->
                                // El error se mostrará en el estado del ViewModel
                            }
                        )
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingUsuario.value = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UserRowCard(
    usuario: Usuario,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nombre de usuario
            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = usuario.username ?: "(Sin nombre)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (usuario.roles.isNotEmpty()) {
                    Text(
                        text = "Roles: ${usuario.roles.joinToString(", ") { it.rolName }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Email
            Text(
                text = usuario.email,
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Botones de acciones
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar usuario",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}