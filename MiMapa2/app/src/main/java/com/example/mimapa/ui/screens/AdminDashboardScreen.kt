package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.Routes.AdminRoute.GenerateAlternativeRoute.toGenerateAlternativeRoute
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.Routes.MainRoute.SetupSearch.toSetupSearch
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.AutoAnimatedCyclicalMarkerMap
import com.example.mimapa.ui.composables.BottomNavigationBar
import com.example.mimapa.ui.composables.BottomNavigationBarAdmin
import com.example.mimapa.ui.composables.RequestLocationPermission
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Panel de administrador")
                },
                actions = { // 'actions' se usa para elementos a la derecha en la TopAppBar
                    IconButton(onClick = {
                        // Llama a la función de extensión de la clase Routes
                        navController.toSettings()
                    }) {
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
                BottomNavigationBarAdmin(navController = navController)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.toGenerateAlternativeRoute() }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Generar ruta alternativa")
            }
        }
    ) { innerPadding ->
        //Sección para el fondo de pantalla
        AppBackground()

        // A partir de aquí se monta la estructura de la pantalla principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Llamada a la función que gestiona los permisos.
            RequestLocationPermission {
                //MapScreen()
                AutoAnimatedCyclicalMarkerMap()
            }
        }
    }
}
