package com.example.mimapa.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.Routes.MainRoute.SetupSearch.toSetupSearch
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.BottomNavigationBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val routePoints = listOf(
    LatLng(36.83936125117198, -2.459356635586987),
    LatLng(36.83992489533426, -2.4589854516073184),
    LatLng(36.84098746382203, -2.4582629274823584),
    LatLng(36.8411210374387, -2.4579178421303896),
    LatLng(36.84099287897318, -2.457597566836406),
    LatLng(36.84031278775575, -2.455978852865853),
    LatLng(36.8396887407288, -2.4559079661625223),
    LatLng(36.83886207319034, -2.456439616437503),
    LatLng(36.838294747160354, -2.456814303297966),
    LatLng(36.83822585728447, -2.4574978536515126),
    LatLng(36.83863514328335, -2.4585105208419527),
    LatLng(36.8390606363066, -2.459351034610018)
)

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

@SuppressLint("UnrememberedMutableState")
@Composable
fun AutoAnimatedCyclicalMarkerMap() {
    val positionGps = LatLng(36.83814, -2.45974)
    // --- 2. Estado de la posición animada del marcador ---
    // Inicia en el primer punto de la lista
    val markerAnimatedPosition = remember { mutableStateOf(routePoints.first()) }

    // --- 3. Animador para la fracción (0f a 1f) ---
    val animationFraction = remember { Animatable(0f) }

    // --- 4. Efecto automático y cíclico ---
    LaunchedEffect(Unit) { // Se ejecuta una vez y se mantiene vivo

        val animationSpec = tween<Float>(durationMillis = 2000) // 2 seg. por tramo

        while (true) {

            // Itera por cada punto de la ruta
            for (i in routePoints.indices) {

                // Punto de inicio = posición actual del marcador
                val startPosition = markerAnimatedPosition.value

                // Punto de destino = el siguiente punto en la lista
                // Usamos el operador "módulo" (%) para que después del último (11),
                // vuelva al primero (0). (11 + 1) % 12 = 0.
                val targetPosition = routePoints[(i + 1) % routePoints.size]

                // Resetea la animación a 0f
                animationFraction.snapTo(0f)

                // Lanza la animación de este tramo
                launch {
                    animationFraction.animateTo(
                        targetValue = 1f,
                        animationSpec = animationSpec
                    ) {
                        // En cada frame, calcula la posición intermedia
                        markerAnimatedPosition.value = SphericalUtil.interpolate(
                            startPosition,
                            targetPosition,
                            value.toDouble() // 'value' es la fracción actual
                        )
                    }
                }

                // Espera a que la animación de 2 segundos termine
                delay(animationSpec.durationMillis.toLong())

                // (Opcional) Una pequeña pausa antes de empezar el siguiente tramo
                delay(500)
            }
        }
    }

    // --- 5. La UI del Mapa ---
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                // Centra la cámara en el primer punto
                position = com.google.android.gms.maps.model.CameraPosition
                    .fromLatLngZoom(routePoints.first(), 17f) // Zoom más cercano
            }
        ) {
            Marker(
                state = remember { MarkerState(position = positionGps) },
                title = "One Marker"
            )
            // Marcador con la posición animada
            Marker(
                state = MarkerState(position = markerAnimatedPosition.value),
                title = "Vehículo en movimiento"
            )
        }
    }
}

@Composable
fun RequestLocationPermission(
    onPermissionGranted: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 1. Estado para saber si el permiso ha sido concedido
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. Crear el "launcher" que solicitará el permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    // 3. Lanzar la petición de permiso cuando el Composable aparece por primera vez
    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 4. Mostrar la UI según el estado del permiso
    if (hasLocationPermission) {
        // --- PERMISO CONCEDIDO ---
        // Ejecuta el contenido que se debe mostrar si el permiso está concedido
        onPermissionGranted()
    } else {
        // --- PERMISO DENEGADO ---
        // Muestra una pantalla explicando por qué necesitas el permiso
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Necesitamos permiso de ubicación para mostrar el mapa.")
            Button(onClick = {
                // Vuelve a solicitar el permiso si el usuario pulsa el botón
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Conceder Permiso")
            }
        }
    }
}
