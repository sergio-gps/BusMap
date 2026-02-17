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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.Routes.MainRoute.ForgotPassword.toForgotPassword
import com.example.mimapa.Routes.MainRoute.Home.toHome
import com.example.mimapa.Routes.MainRoute.SignUp.toSignUp
import com.example.mimapa.ui.composables.AppBackground
import com.example.mimapa.util.SecureSessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    // Sección para el fondo de pantalla
    AppBackground()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
                val password = remember { mutableStateOf("") }
                var jwt: String?
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                LoginHeader()
                Spacer(modifier = Modifier.height(20.dp))
                LoginFields(
                    email.value,
                    password.value,
                    onEmailChange = { email.value = it },
                    onPasswordChange = { password.value = it },
                    onForgotPasswordClick = {
                        navController.toForgotPassword()
                    }
                )
                LoginFooter(
                    onSignInClick = {
                        scope.launch {

                            jwt = try {
                                LlamadasAPI.logIn(email.value, password.value)
                            }catch (e: Exception){
                                Log.e("LoginScreen", "Error al iniciar sesión: ${e.message}", e)
                                null
                            }

                            if(jwt == "0"){
                                Toast.makeText(
                                    context,
                                    "Error en login, credenciales erróneas o respuesta no exitosa",
                                    Toast.LENGTH_LONG // La duración (SHORT o LONG)
                                ).show()
                            } else if (jwt != null) {
                                Log.d("LoginScreen", "Botón de login clickeado. Email: ${email.value}")
                                val sessionManager = SecureSessionManager
                                
                                // Decodificar el JWT para obtener el rol
                                val userRole = JwtHelper.getUserRole(jwt!!)
                                val isAdmin = JwtHelper.isAdmin(jwt!!)

                                sessionManager.saveAuthToken(context, jwt!!, email.value)

                                // Guardar también el rol si lo necesitas
                                // sessionManager.saveUserRole(context, userRole)

                                if (isAdmin) {
                                    Log.d("LoginScreen", "Usuario admin detectado")
                                    // navController.toAdminHome() // Si tienes pantalla diferente
                                } else {
                                    Log.d("LoginScreen", "Usuario normal detectado")
                                }

                                navController.toHome()
                            } else {
                                Log.d("LoginScreen", "Login o conexión fallido. Email: ${email.value}")
                                Toast.makeText(
                                    context,
                                    "Error de conexión",
                                    Toast.LENGTH_LONG // La duración (SHORT o LONG)
                                ).show()
                            }
                        }
                    },
                    onSignUpClick = {
                        navController.toSignUp()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Bienvenido!", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "Inicia sesión para continuar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LoginFields(
    email: String, password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column {
        TextField(
            value = email,
            label = "Email",
            placeholder = "Introduce tu email",
            onValueChange = onEmailChange,
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            label = "Password",
            placeholder = "Introduce tu contraseña",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Password")
            }
        )

        TextButton(onClick = onForgotPasswordClick, modifier = Modifier.align(Alignment.End)) {
            Text(text = "¿Olvidaste la contraseña?")
        }
    }
}

@Composable
fun LoginFooter(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Inicia sesión")
        }
        TextButton(onClick = onSignUpClick) {
            Text(text = "Si no tienes una cuenta, pincha aquí")
        }
    }
}

@Composable
fun TextField(
    value: String,
    label: String,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.background,
        )
    )
}
