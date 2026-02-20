package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.Routes.MainRoute.SetupSearch.toSetupSearch
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.AutoAnimatedCyclicalMarkerMap
import com.example.mimapa.ui.composables.BottomNavigationBar
import com.example.mimapa.ui.composables.RequestLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Pantalla principal")
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
                    BottomNavigationBar(navController = navController)
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.toSetupSearch() }) {
                    Icon(Icons.Default.Search, contentDescription = "Busca tu próximo destino")
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

@Composable
fun MapScreen() {
    val position = LatLng(36.83814, -2.45974)

    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, 15f)
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        Marker(
            state = remember { MarkerState(position = position) },
            title = "One Marker"
        )
    }
}
