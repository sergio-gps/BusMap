package com.example.mimapa.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.MainRoute.Login.toLogin
import com.example.mimapa.Routes.MainRoute.Settings.toSettings
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.ReusableTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController) {
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
        //AppBackground()

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
                    modifier = Modifier.padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val currentPassword = remember { mutableStateOf("") }
                    val password = remember { mutableStateOf("") }
                    val password2 = remember { mutableStateOf("") }

                    var isCurrentPasswordError by remember { mutableStateOf(false) }
                    var isPasswordError by remember { mutableStateOf(false) }
                    var isPassword2Error by remember { mutableStateOf(false) }

                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    ResetPasswordHeader()
                    Spacer(modifier = Modifier.height(20.dp))
                    ResetPasswordFields(
                        currentPassword = currentPassword.value,
                        password = password.value,
                        password2 = password2.value,
                        onCurrentPasswordChange = { currentPassword.value = it },
                        onPasswordChange = { password.value = it },
                        onPassword2Change = { password2.value = it },
                        isCurrentPasswordError = isCurrentPasswordError,
                        isPasswordError = isPasswordError,
                        isPassword2Error = isPassword2Error
                    )
                    ResetPasswordFooter {

                        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\\S+$).{8,}".toRegex()
                        isCurrentPasswordError = !currentPassword.value.matches(passwordRegex)
                        isPasswordError = !password.value.matches(passwordRegex)

                        isPassword2Error = password.value != password2.value || password2.value.isBlank()

                        val hasError = isCurrentPasswordError || isPasswordError || isPassword2Error

                        if (!hasError){
                            scope.launch {
                                val jwt = LlamadasAPI.resetPassword(currentPassword.value, password.value, context)
                                if(jwt != null) {
                                    Log.d(
                                        "ResetPasswordScreen",
                                        "Botón de resetear contraseña clickeado."
                                    )
                                    navController.toSettings()
                                    Toast.makeText(
                                        context,
                                        "Cambio exitoso de la contraseña",
                                        Toast.LENGTH_LONG // La duración (SHORT o LONG)
                                    ).show()
                                }else{
                                    Log.d("ResetPasswordScreen", "Cambio de contraseña fallido.")
                                    Toast.makeText(
                                        context,
                                        "Cambio fallido posible error de conexión",
                                        Toast.LENGTH_LONG // La duración (SHORT o LONG)
                                    ).show()
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
fun ResetPasswordHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Introduzca la nueva contraseña", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "La nueva contraseña debe tener mínimo 8 caracteres, 1 mayúscula, 1 número y 1 caracter especial.",
            fontSize = 16.sp, fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ResetPasswordFields(
    currentPassword: String,
    password: String,
    password2: String,
    onCurrentPasswordChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPassword2Change: (String) -> Unit,
    isCurrentPasswordError: Boolean,
    isPasswordError: Boolean,
    isPassword2Error: Boolean
) {
    Column {
        ReusableTextField(
            value = currentPassword,
            label = "Contraseña actual",
            placeholder = "Introduzca su contraseña actual.",
            onValueChange = onCurrentPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
            isError = isCurrentPasswordError,
            errorText = "La contraseña no cumple los requisitos"
        )

        Spacer(modifier = Modifier.height(10.dp))

        ReusableTextField(
            value = password,
            label = "Nueva contraseña",
            placeholder = "Introduzca su nueva contraseña.",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
            isError = isPasswordError,
            errorText = "La contraseña no cumple los requisitos"
        )

        Spacer(modifier = Modifier.height(10.dp))

        ReusableTextField(
            value = password2,
            label = "Nueva contraseña",
            placeholder = "Repita nueva contraseña.",
            onValueChange = onPassword2Change,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
            isError = isPassword2Error,
            errorText = "Las contraseñas no coinciden"
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ResetPasswordFooter(onResetPasswordClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onResetPasswordClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Cambiar contraseña")
        }
    }
}