package com.example.mimapa.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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

