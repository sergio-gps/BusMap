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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.ui.composables.ReusableTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de usuario") },
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
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()), // Añadimos scroll
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
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val email = remember { mutableStateOf("") }
                    val password = remember { mutableStateOf("") }
                    val password2 = remember { mutableStateOf("") }

                    var isEmailError by remember { mutableStateOf(false) }
                    var isPasswordError by remember { mutableStateOf(false) }
                    var isPassword2Error by remember { mutableStateOf(false) }

                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    SignUpHeader()
                    Spacer(modifier = Modifier.height(20.dp))
                    SignUpFields(
                        email = email.value,
                        password = password.value,
                        password2 = password2.value,
                        onEmailChange = { email.value = it },
                        onPasswordChange = { password.value = it },
                        onPassword2Change = { password2.value = it },
                        isEmailError = isEmailError,
                        isPasswordError = isPasswordError,
                        isPassword2Error = isPassword2Error
                    )
                    SignUpFooter {
                        // Validaciones
                        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}".toRegex()
                        isEmailError = !email.value.matches(emailRegex)

                        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\\S+$).{8,}".toRegex()
                        isPasswordError = !password.value.matches(passwordRegex)

                        isPassword2Error = password.value != password2.value || password2.value.isBlank()

                        val hasError = isEmailError || isPasswordError || isPassword2Error

                        if (!hasError) {
                            scope.launch {
                                val jwt = LlamadasAPI.signUp(email.value, password.value, password2.value)
                                if (jwt != null) {
                                    Log.d("SignUpScreen", "Botón de registro clickeado. Email: ${email.value}")
                                    navController.toLogin()
                                } else {
                                    Log.d("SignUpScreen", "Registro fallido. Email: ${email.value}")
                                    Toast.makeText(
                                        context,
                                        "Registro fallido posible error de conexión",
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
fun SignUpHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Introduzca sus datos", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "La contraseña debe tener mínimo 8 caracteres, 1 mayúscula, 1 número y 1 caracter especial.",
            fontSize = 16.sp, fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SignUpFields(
    email: String,
    password: String,
    password2: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPassword2Change: (String) -> Unit,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    isPassword2Error: Boolean
) {
    Column {
        ReusableTextField(
            value = email,
            label = "Email",
            placeholder = "Introduzca su email",
            onValueChange = onEmailChange,
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            isError = isEmailError,
            errorText = "Email inválido"
        )

        Spacer(modifier = Modifier.height(10.dp))

        ReusableTextField(
            value = password,
            label = "Contraseña",
            placeholder = "Introduzca contraseña",
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
            label = "Contraseña",
            placeholder = "Repita contraseña",
            onValueChange = onPassword2Change,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password2") },
            isError = isPassword2Error,
            errorText = "Las contraseñas no coinciden"
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun SignUpFooter(onSignUpClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onSignUpClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Registrarse")
        }
    }
}