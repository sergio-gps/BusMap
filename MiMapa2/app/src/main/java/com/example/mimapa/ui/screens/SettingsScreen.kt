package com.example.mimapa.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.Routes.MainRoute.About.toAbout
import com.example.mimapa.Routes.MainRoute.Credits.toCredits
import com.example.mimapa.Routes.MainRoute.Login.toLogin
import com.example.mimapa.Routes.MainRoute.ResetPassword.toResetPassword
import com.example.mimapa.util.SecureSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        //Sección para el fondo de pantalla
        //AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Esta es la Pantalla de Configuración")

            Spacer(modifier = Modifier.height(20.dp))

            ListItem(
                headlineContent = { Text("Créditos") },
                supportingContent = { Text("Licencias de las imágenes y recursos utilizados") },
                modifier = Modifier.clickable {
                    navController.toCredits()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ListItem(
                headlineContent = { Text("Acerca de") },
                supportingContent = { Text("Información sobre la aplicación y su desarrollador") },
                modifier = Modifier.clickable {
                    navController.toAbout()
                }
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = {navController.toResetPassword()},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD20F00))
            ) {
                Text(text = "Cambia tu contraseña")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    SecureSessionManager.clearAuthToken(context)
                    navController.toLogin()
                    Toast.makeText(
                        context,
                        "Sesión cerrada",
                        Toast.LENGTH_LONG // La duración (SHORT o LONG)
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD20F00))) {
                Text(text= "Cerrar sesión")
            }
        }
    }
}