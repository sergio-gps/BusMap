package com.example.mimapa.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.MainRoute.Login.toLogin
import com.example.mimapa.ui.composables.AppBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Olvidó su contraseña") },
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

        // Sección para el fondo de pantalla
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(28.dp)
                    .clip(
                        CutCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    // Aplicamos la transparencia al background y no al box
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val email = remember { mutableStateOf("") }
                    val scope = rememberCoroutineScope()

                    ForgotPasswordHeader()
                    Spacer(modifier = Modifier.height(20.dp))
                    ForgotPasswordFields(
                        email = email.value,
                        onEmailChange = { email.value = it }
                    )
                    ForgotPasswordFooter(
                        onForgotPasswordClick = {
                            scope.launch {
                                LlamadasAPI.forgotPassword(email = email.value)
                                Log.d("ForgotPasswordScreen", "Botón de recuperar contraseña clickeado.") // Log adicional desde la UI

                                navController.toLogin()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Introduzca su email", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ForgotPasswordFields(
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column {
        TextField(
            value = email,
            label = "Email",
            placeholder = "Introduzca su email para recuperar la contraseña",

            onValueChange = onEmailChange,
            //visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Go
            ),
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ForgotPasswordFooter(onForgotPasswordClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onForgotPasswordClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Solicitar cambio de contraseña")
        }
    }
}