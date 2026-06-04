package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Historial")
                },
                actions = { // 'actions' se usa para elementos a la derecha en la TopAppBar
                    IconButton(onClick = {
                        // Llama a la función de extensión de la clase Routes
                        navController.toSettings()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
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
        }/*,
            floatingActionButton = {
                FloatingActionButton(onClick = { presses++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }*/
    ) { innerPadding ->
        //Sección para el fondo de pantalla
        AppBackground()

        // A partir de aquí se monta la estructura de la pantalla principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            val mockHistory = listOf(
                RouteHistoryMock("1", "V-101", "Paseo de Almería", "Universidad", "20/05/2026 08:30"),
                RouteHistoryMock("2", "V-102", "Universidad", "Estación Intermodal", "20/05/2026 14:15"),
                RouteHistoryMock("3", "V-101", "Estación Intermodal", "Hospital", "19/05/2026 09:00"),
                RouteHistoryMock("4", "V-103", "Hospital", "Paseo de Almería", "18/05/2026 18:45"),
                RouteHistoryMock("5", "V-102", "Paseo de Almería", "Universidad", "18/05/2026 08:15")
            )

            // Cabecera de la lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ruta",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Lista de historial
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockHistory) { historyItem ->
                    HistoryRowCard(item = historyItem)
                }
            }
        }
    }
}

data class RouteHistoryMock(
    val id: String,
    val vehiculoId: String,
    val origen: String,
    val destino: String,
    val fecha: String
)

@Composable
fun HistoryRowCard(item: RouteHistoryMock) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Origen y Destino
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = "${item.origen} - ${item.destino}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                Text(
                    text = item.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ID y Vehículo
            Column(
                modifier = Modifier.weight(1.5f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "ID: ${item.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Vehículo: ${item.vehiculoId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}