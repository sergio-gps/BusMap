package com.example.mimapa

import android.content.Context
import android.util.Log
import com.example.mimapa.BuildConfig.MAPS_API_KEY
import com.example.mimapa.data.model.Email
import com.example.mimapa.data.model.Passwords
import com.example.mimapa.data.model.Token
import com.example.mimapa.data.model.UserCredentials
import com.example.mimapa.util.GenerateRoute
import com.example.mimapa.util.SecureSessionManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LlamadasAPI {

    private val client = OkHttpClient()

    /**
     * Registro de un usuario e imprime un mensaje en la consola.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @param password2 La confirmación de la contraseña del usuario.
     * @return El token JWT como String si el registro es exitoso, null en caso contrario.
     */
    suspend fun signUp(email: String, password: String, password2: String): String? {
        Log.d("LlamadasAPI", "Intentando registrar usuario...")
        Log.d("LlamadasAPI", "Email: $email")
        //Log.d("LlamadasAPI", "Password: $password")

        val contrasenasCoinciden = password == password2
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
        val esPasswordValido = passwordRegex.matches(password)

        if (!esPasswordValido) Log.w("LlamadasAPI", "La contraseña no cumple con los requisitos: mínimo 8 caracteres, 1 mayúscula, 1 número y 1 caracter especial.")
        if (!contrasenasCoinciden) Log.w("LlamadasAPI", "Las contraseñas no coinciden.")

        if (esPasswordValido && contrasenasCoinciden) {
            val json = Json.encodeToString(UserCredentials(email, password))

            //http://olympia.jpramez.dev:8080/register
            //http://10.0.2.2:8080/register
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/register").header("Content-Type", "application/json")
                .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            return suspendCancellableCoroutine { continuation ->
                val call = client.newCall(request)
                call.enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("LlamadasAPI", "Fallo en la llamada de registro", e)
                        // Si la coroutine fue cancelada
                        if (continuation.isCancelled) return
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (continuation.isCancelled) return

                        response.use {
                            if (!response.isSuccessful) {
                                Log.w(
                                    "LlamadasAPI",
                                    "Error en registro, credenciales erróneas o respuesta no exitosa: $response"
                                )
                                continuation.resume(null) // O lanzar una excepción específica
                            } else {
                                val responseBody = response.body?.string()
                                if (responseBody != null) {
                                    try {
                                        val tokenResponse = Json.decodeFromString<Token>(responseBody)
                                        Log.i("LlamadasAPI", "Registro exitoso, token recibido: ${tokenResponse.token}")
                                        continuation.resume(tokenResponse.token)
                                    } catch (e: SerializationException) {
                                        Log.e("LlamadasAPI", "Error al decodificar la respuesta JSON de registro: ${e.message}", e)
                                        Log.e("LlamadasAPI", "JSON recibido: $responseBody")
                                        continuation.resume(null)
                                    } catch (e: Exception) { // Otra excepción inesperada
                                        Log.e("LlamadasAPI", "Error inesperado durante el procesamiento de la respuesta de registro: ${e.message}", e)
                                        continuation.resume(null)
                                    }
                                } else {
                                    Log.w(
                                        "LlamadasAPI",
                                        "Cuerpo de respuesta vacío en login exitoso."
                                    )
                                    continuation.resume(null)
                                }
                            }
                        }
                    }
                })

                // Cancela la llamada de OkHttp
                continuation.invokeOnCancellation {
                    try {
                        call.cancel()
                    } catch (ex: Throwable) {
                        // Ignorar si la cancelación falla
                    }
                }
            }
        }
        return null
    }

    /**
     * Inicia sesión de un usuario llamando a la API y devuelve el token JWT.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return El token JWT como String si el login es exitoso, null en caso contrario.
     */
    suspend fun logIn(email: String, password: String): String? {
        Log.d("LlamadasAPI", "Intentando iniciar sesión con coroutines...")
        Log.d("LlamadasAPI", "Email: $email")

        val json = Json.encodeToString(UserCredentials(username = email, password = password))

        //http://olympia.jpramez.dev:8080/login
        //http://10.0.2.2:8080/login
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/login").header("Content-Type", "application/json")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en la llamada de login", e)
                    // Si la coroutine fue cancelada
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return

                    response.use {
                        if (!response.isSuccessful) {
                            Log.w(
                                "LlamadasAPI",
                                "Error en login, credenciales erróneas o respuesta no exitosa: $response"
                            )
                            continuation.resume("0")

                        } else {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                try {
                                    val tokenResponse = Json.decodeFromString<Token>(responseBody)
                                    Log.i("LlamadasAPI", "Login exitoso, token recibido: ${tokenResponse.token}")
                                    continuation.resume(tokenResponse.token)
                                } catch (e: SerializationException) {
                                    Log.e("LlamadasAPI", "Error al decodificar la respuesta JSON de login: ${e.message}", e)
                                    Log.e("LlamadasAPI", "JSON recibido: $responseBody")
                                    continuation.resume(null)
                                } catch (e: Exception) {
                                    Log.e("LlamadasAPI", "Error inesperado durante el procesamiento de la respuesta de login: ${e.message}", e)
                                    continuation.resume(null)
                                }
                            } else {
                                Log.w(
                                    "LlamadasAPI",
                                    "Cuerpo de respuesta vacío en login exitoso."
                                )
                                continuation.resume(null)
                            }
                        }
                    }
                }
            })

            // Cancela la llamada de OkHttp
            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (ex: Throwable) {
                    // Ignorar si la cancelación falla
                }
            }
        }
    }

    /**
     * Hace la recuperación de contraseña de un usuario e imprime un mensaje en la consola.
     *
     * @param currentPassword La contraseña actual del usuario.
     * @param newPassword La nueva contraseña del usuario.
     * @return El token JWT como String si la recuperación de contraseña es exitosa, null en caso contrario.
     */
    suspend fun resetPassword(currentPassword: String, newPassword: String, context: Context): String? {
        // Respuesta erronea si las contraseñas coinciden
        if (currentPassword == newPassword){
            Log.e("LlamadasAPI", "Las contraseñas coinciden.")
            return null
        }

        val json = Json.encodeToString(value = Passwords(
            currentPassword = currentPassword,
            newPassword = newPassword)
        )

        //http://olympia.jpramez.dev:8080/
        //http://10.0.2.2:8080/
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/change-password").header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + SecureSessionManager.getAuthToken(context))
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en la llamada de resetPassword", e)
                    // Si la coroutine fue cancelada
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return

                    response.use {
                        if (!response.isSuccessful) {
                            Log.w(
                                "LlamadasAPI",
                                "Error en resetPassword, contraseña errónea o respuesta no exitosa: $response"
                            )
                            continuation.resume(null)
                        } else {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                try {
                                    Log.i("LlamadasAPI", "resettPassword exitoso, string recibido: $responseBody")
                                    continuation.resume(responseBody)
                                } catch (e: SerializationException) {
                                    Log.e("LlamadasAPI", "Error al decodificar la respuesta JSON de resettPassword: ${e.message}", e)
                                    Log.e("LlamadasAPI", "JSON recibido: $responseBody")
                                    continuation.resume(null)
                                }
                            } else {
                                Log.w("LlamadasAPI", "Cuerpo de respuesta vacío en resetPassword.")
                                continuation.resume(null)
                            }
                        }
                    }
                }
            })
            // Cancela la llamada de OkHttp
            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (ex: Throwable) {
                    // Ignorar si la cancelación falla
                }
            }
        }
    }

    /**
     * Envía una solicitud para restablecer la contraseña de un usuario.
     *
     * @param email El email del usuario.
     * @return Un string recibido como respuesta si la solicitud es exitosa, null en caso contrario.
     */
    suspend fun forgotPassword(email: String): String? {
        val json = Json.encodeToString(value = Email(email = email))

        //http://olympia.jpramez.dev:8080/
        //http://10.0.2.2:8080/
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/forgot-password").header("Content-Type", "application/json")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en la llamada de forgotPassword", e)
                    // Si la coroutine fue cancelada
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return

                    response.use {
                        if (!response.isSuccessful) {
                            Log.w(
                                "LlamadasAPI",
                                "Error en forgotPassword, credenciales erróneas o respuesta no exitosa: $response"
                            )
                            continuation.resume(null)
                        } else {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                try {
                                    Log.i("LlamadasAPI", "forgotPassword exitoso, string recibido: $responseBody")
                                    continuation.resume(responseBody)
                                } catch (e: SerializationException) {
                                    Log.e("LlamadasAPI", "Error al decodificar la respuesta JSON de forgotPassword: ${e.message}", e)
                                    Log.e("LlamadasAPI", "JSON recibido: $responseBody")
                                    continuation.resume(null)
                                } catch (e: Exception) {
                                    Log.e("LlamadasAPI", "Error inesperado durante el procesamiento de la respuesta de forgotPassword: ${e.message}", e)
                                    continuation.resume(null)
                                }
                            } else {
                                Log.w(
                                    "LlamadasAPI",
                                    "Cuerpo de respuesta vacío en forgotPassword exitoso."
                                )
                                continuation.resume(null)
                            }
                        }
                    }
                }
            })

            // Cancela la llamada de OkHttp
            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (ex: Throwable) {
                    // Ignorar si la cancelación falla
                }
            }
        }
    }

    /**
     * Calcula una ruta entre un origen y un destino, con posibles puntos intermedios.
     *
     * @param origin El punto de partida de la ruta.
     * @param destination El punto de destino de la ruta.
     * @param intermediates Una lista de puntos intermedios opcionales.
     * @param context El contexto de la aplicación.
     * @return Un String con la respuesta JSON de la API de Routes, o null si hay un error.
     */
    suspend fun computeRoute(origin: LatLng, destination: LatLng, intermediates: List<LatLng> = emptyList(), context: Context): String? {
        Log.d("LlamadasAPI", "Calcular ruta")

        val routeGenerator = GenerateRoute()
        val requestBody = routeGenerator.createRoutesRequestBody(origin, destination, intermediates).toString()

        val request = Request.Builder()
            .url("https://routes.googleapis.com/directions/v2:computeRoutes")
            .header("Content-Type", "application/json")
            .header("X-Goog-Api-Key", MAPS_API_KEY)
            //Estas dos cabeceras se incluyen porque no se puede hacer la llamada a la API desde un servidor proxy seguro.
            //Cuando eso se corrija no hace falta que se incluyan las cabeceras.
            .header("X-Android-Package", "com.example.mimapa")
            .header("X-Android-Cert", "80F9BBB30DCA36EB0D3395A241F0F0E79B62F83F")
            //
            .header("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline") //máscara para la respuesta de la api
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en la llamada de computeRoute", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return

                    response.use {
                        val responseBodyString = it.body?.string()
                        if (!it.isSuccessful) {
                            Log.e("LlamadasAPI", "Error en computeRoute: $responseBodyString")
                            continuation.resume(null)
                        } else {
                            if (responseBodyString != null) {
                                Log.i("LlamadasAPI", "Cálculo de ruta exitoso.")
                                continuation.resume(responseBodyString)
                            } else {
                                Log.w("LlamadasAPI", "Cuerpo de respuesta vacío en computeRoute exitoso.")
                                continuation.resume(null)
                            }
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (ex: Throwable) {
                    // Ignorar
                }
            }
        }
    }
}
