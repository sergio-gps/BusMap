package com.example.mimapa

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mimapa.BuildConfig.DEBUG
import com.example.mimapa.BuildConfig.MAPS_API_KEY
import com.example.mimapa.data.model.Email
import com.example.mimapa.data.model.Linea
import com.example.mimapa.data.model.LoginResult
import com.example.mimapa.data.model.Parada
import com.example.mimapa.data.model.Passwords
import com.example.mimapa.data.model.TipoVehiculo
import com.example.mimapa.data.model.Token
import com.example.mimapa.data.model.UserCredentials
import com.example.mimapa.data.model.Usuario
import com.example.mimapa.data.model.VehiculoAdmin
import com.example.mimapa.data.model.VehiculoAdminRequest
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
    private val json = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = "http://10.0.2.2:8080"
    private const val API_BASE_URL = "$BASE_URL/api"

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

        val contrasenasCoinciden = password == password2
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
        val esPasswordValido = passwordRegex.matches(password)

        if (!esPasswordValido) Log.w("LlamadasAPI", "La contraseña no cumple con los requisitos: mínimo 8 caracteres, 1 mayúscula, 1 número y 1 caracter especial.")
        if (!contrasenasCoinciden) Log.w("LlamadasAPI", "Las contraseñas no coinciden.")

        if (esPasswordValido && contrasenasCoinciden) {
            val json = Json.encodeToString(UserCredentials(email, password))

            val request = Request.Builder()
                .url("$BASE_URL/register").header("Content-Type", "application/json")
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
                    } catch (_: Throwable) {
                        // Ignorar si la cancelación falla
                    }
                }
            }
        }
        return null
    }

    /**
     * Inicia sesión de un usuario llamando a la API y devuelve el token JWT y los roles del usuario.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return Un [LoginResult] con el token JWT y los roles si el login es exitoso, null en caso contrario.
     */
    suspend fun logIn(email: String, password: String): LoginResult? {
        Log.d("LlamadasAPI", "Intentando iniciar sesión con coroutines...")
        Log.d("LlamadasAPI", "Email: $email")

        val json = Json.encodeToString(UserCredentials(email = email, password = password))

        val request = Request.Builder()
            .url("$BASE_URL/login").header("Content-Type", "application/json")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en la llamada de login", e)
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
                            // Token "0" indica credenciales erróneas
                            continuation.resume(LoginResult(token = "0", role = null))
                        } else {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                try {
                                    val tokenResponse = Json.decodeFromString<LoginResult>(responseBody)
                                    Log.i("LlamadasAPI", "Login exitoso, token: ${tokenResponse.token}, roles: ${tokenResponse.role}")
                                    continuation.resume(tokenResponse)
                                } catch (e: SerializationException) {
                                    Log.e("LlamadasAPI", "Error al decodificar la respuesta JSON de login: ${e.message}", e)
                                    Log.e("LlamadasAPI", "JSON recibido: $responseBody")
                                    continuation.resume(null)
                                } catch (e: Exception) {
                                    Log.e("LlamadasAPI", "Error inesperado durante el procesamiento de la respuesta de login: ${e.message}", e)
                                    continuation.resume(null)
                                }
                            } else {
                                Log.w("LlamadasAPI", "Cuerpo de respuesta vacío en login exitoso.")
                                continuation.resume(null)
                            }
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
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

        val request = Request.Builder()
            .url("$BASE_URL/change-password").header("Content-Type", "application/json")
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
                } catch (_: Throwable) {
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

        val request = Request.Builder()
            .url("$BASE_URL/forgot-password").header("Content-Type", "application/json")
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
                } catch (_: Throwable) {
                    // Ignorar si la cancelación falla
                }
            }
        }
    }

    /**
     * Construye un Request.Builder autorizado con el token JWT.
     *
     * @param url La URL del endpoint a llamar.
     * @param context El contexto de la aplicación para obtener el token.
     * @return Un Request.Builder configurado con headers de autenticación.
     */
    private fun authorizedRequestBuilder(url: String, context: Context): Request.Builder {
        val token = SecureSessionManager.getAuthToken(context)
        return Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
    }

    /**
     * Obtiene la lista de todas las paradas del sistema.
     *
     * @param context El contexto de la aplicación.
     * @return Una lista de paradas, o lista vacía si hay error.
     */
    suspend fun getParadas(context: Context): List<Parada> {
        val request = authorizedRequestBuilder("$API_BASE_URL/paradas", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getParadas", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            Log.w("LlamadasAPI", "Error en getParadas: $it")
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<List<Parada>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando listado de paradas", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Crea una nueva parada en el sistema.
     *
     * @param parada El objeto Parada con los datos de la nueva parada.
     * @param context El contexto de la aplicación.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    suspend fun createParada(parada: Parada, context: Context): Boolean {
        val requestBody = Json.encodeToString(parada)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/paradas", context).post(requestBody).build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en createParada", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Actualiza los datos de una parada existente.
     *
     * @param paradaId El ID de la parada a actualizar.
     * @param parada El objeto Parada con los datos actualizados.
     * @param context El contexto de la aplicación.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    suspend fun updateParada(paradaId: Int, parada: Parada, context: Context): Boolean {
        val requestBody = Json.encodeToString(parada)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/paradas/$paradaId", context)
            .put(requestBody)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en updateParada", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Elimina una parada del sistema.
     *
     * @param paradaId El ID de la parada a eliminar.
     * @param context El contexto de la aplicación.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    suspend fun deleteParada(paradaId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/paradas/$paradaId", context)
            .delete()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en deleteParada", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Importa un listado de paradas desde un archivo JSON.
     *
     * @param rawJson El contenido JSON con las paradas a importar.
     * @param context El contexto de la aplicación.
     * @return true si la importación fue exitosa, false en caso contrario.
     */
    suspend fun importParadasJson(rawJson: String, context: Context): Boolean {
        val requestBody = rawJson.toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/paradas/import", context)
            .post(requestBody)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en importParadasJson", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
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
     * @return Un String con la respuesta JSON de la API de Routes, o null si hay un error.
     */
    suspend fun computeRoute(
        origin: LatLng,
        destination: LatLng,
        intermediates: List<LatLng> = emptyList(),
        requestedReferenceRoutes: List<String> = emptyList(),
        departureTime: String? = null,
        arrivalTime: String? = null
    ): String? {
        Log.d("LlamadasAPI", "Calcular ruta")

        val routeGenerator = GenerateRoute()
        val requestBody = routeGenerator.createRoutesRequestBody(
            origin,
            destination,
            intermediates,
            requestedReferenceRoutes = requestedReferenceRoutes,
            departureTime = departureTime,
            arrivalTime = arrivalTime
        ).toString()

        if (DEBUG) {
            Log.d("LlamadasAPI", "Request body to Google: $requestBody")
        }

        val request = Request.Builder()
            .url("https://routes.googleapis.com/directions/v2:computeRoutes")
            .header("Content-Type", "application/json")
            .header("X-Goog-Api-Key", MAPS_API_KEY)
            //Estas dos cabeceras se incluyen porque no se puede hacer la llamada a la API desde un servidor proxy seguro.
            //Cuando eso se corrija no hace falta que se incluyan las cabeceras.
            .header("X-Android-Package", "com.example.mimapa")
            .header("X-Android-Cert", "80F9BBB30DCA36EB0D3395A241F0F0E79B62F83F")
            //
            .header(
                "X-Goog-FieldMask",
                "routes.duration," +
                    "routes.distanceMeters," +
                    "routes.polyline.encodedPolyline," +
                    "routes.travelAdvisory.fuelConsumptionMicroliters," +
                    "routes.travelAdvisory.speedReadingIntervals," +
                    "routes.legs.travelAdvisory.speedReadingIntervals"
            ) //máscara para la respuesta de la api
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
                        if (DEBUG && responseBodyString != null) {
                            val maxLogChars = 1500
                            val sample = responseBodyString.take(maxLogChars)
                            val hasFuelField = responseBodyString.contains("fuelConsumptionMicroliters")
                            Log.d(
                                "LlamadasAPI",
                                "computeRoute raw JSON (${responseBodyString.length} chars, fuelField=$hasFuelField): $sample"
                            )
                        }

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
                } catch (_: Throwable) {
                    // Ignorar
                }
            }
        }
    }

    /**
     * Obtiene la lista de todas las líneas del sistema.
     *
     * @param context El contexto de la aplicación.
     * @return Una lista de líneas, o lista vacía si hay error.
     */
    suspend fun getLineas(context: Context): List<Linea> {
        val request = authorizedRequestBuilder("$API_BASE_URL/lineas", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getLineas", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            Log.w("LlamadasAPI", "Error en getLineas: $it")
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body.string()
                        try {
                            continuation.resume(json.decodeFromString<List<Linea>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando listado de lineas", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Obtiene una linea por su id.
     *
     * @param lineaId El id de la linea.
     * @param context El contexto de la aplicación.
     * @return La linea solicitada, o null si no existe.
     */
    suspend fun getLinea(lineaId: Int, context: Context): Linea? {
        val request = authorizedRequestBuilder("$API_BASE_URL/lineas/$lineaId", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            Log.w("LlamadasAPI", "Error en getLinea: $it")
                            continuation.resume(null)
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<Linea>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando la linea", e)
                            continuation.resume(null)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }


    /**
     * Crea una nueva línea en el sistema.
     *
     * @param linea El objeto Linea con los datos de la nueva línea.
     * @param context El contexto de la aplicación.
     * @return El objeto Linea creado, o null si hay error.
     */
    suspend fun createLinea(linea: Linea, context: Context): Linea? {
        val requestBody = Json.encodeToString(linea)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/lineas", context)
            .post(requestBody)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en createLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(null)
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<Linea>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando linea creada", e)
                            continuation.resume(null)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Actualiza los datos de una línea existente.
     *
     * @param lineaId El ID de la línea a actualizar.
     * @param linea El objeto Linea con los datos actualizados.
     * @param context El contexto de la aplicación.
     * @return El objeto Linea actualizado, o null si hay error.
     */
    suspend fun updateLinea(lineaId: Int, linea: Linea, context: Context): Linea? {
        val requestBody = Json.encodeToString(linea)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/lineas/$lineaId", context)
            .put(requestBody)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en updateLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(null)
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<Linea>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando linea actualizada", e)
                            continuation.resume(null)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Elimina una línea del sistema.
     *
     * @param lineaId El ID de la línea a eliminar.
     * @param context El contexto de la aplicación.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    suspend fun deleteLinea(lineaId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/lineas/$lineaId", context)
            .delete()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en deleteLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use { continuation.resume(it.isSuccessful) }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Añade una parada a una línea existente.
     *
     * @param lineaId El ID de la línea.
     * @param paradaId El ID de la parada a añadir.
     * @param context El contexto de la aplicación.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    suspend fun addParadaToLinea(lineaId: Int, paradaId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/lineas/$lineaId/paradas/$paradaId", context)
            .post("".toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en addParadaToLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Elimina una parada de una línea existente.
     *
     * @param lineaId El ID de la línea.
     * @param paradaId El ID de la parada a eliminar.
     * @param context El contexto de la aplicación.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    suspend fun removeParadaFromLinea(lineaId: Int, paradaId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/lineas/$lineaId/paradas/$paradaId", context)
            .delete()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en removeParadaFromLinea", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Obtiene la lista de todos los vehículos enriquecidos para gestión admin.
     */
    suspend fun getVehiculos(context: Context): List<VehiculoAdmin> {
        val request = authorizedRequestBuilder("$API_BASE_URL/vehiculos", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getVehiculos", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<List<VehiculoAdmin>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando listado de vehiculos", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Busca vehículos por texto libre en backend.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun searchVehiculos(query: String, context: Context): List<VehiculoAdmin> {
        val encodedQuery = java.net.URLEncoder.encode(query, Charsets.UTF_8)
        val request = authorizedRequestBuilder("$API_BASE_URL/vehiculos?query=$encodedQuery", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en searchVehiculos", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<List<VehiculoAdmin>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando busqueda de vehiculos", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Crea un vehículo con su tipo e info.
     */
    suspend fun createVehiculo(requestBody: VehiculoAdminRequest, context: Context): VehiculoAdmin? {
        val body = Json.encodeToString(requestBody)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/vehiculos", context)
            .post(body)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en createVehiculo", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(null)
                            return
                        }

                        val bodyResponse = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<VehiculoAdmin>(bodyResponse))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando vehiculo creado", e)
                            continuation.resume(null)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Actualiza un vehículo con su tipo e info.
     */
    suspend fun updateVehiculo(vehiculoId: Int, requestBody: VehiculoAdminRequest, context: Context): VehiculoAdmin? {
        val body = Json.encodeToString(requestBody)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/vehiculos/$vehiculoId", context)
            .put(body)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en updateVehiculo", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(null)
                            return
                        }

                        val bodyResponse = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<VehiculoAdmin>(bodyResponse))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando vehiculo actualizado", e)
                            continuation.resume(null)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Elimina un vehículo y su info asociada.
     */
    suspend fun deleteVehiculo(vehiculoId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/vehiculos/$vehiculoId", context)
            .delete()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en deleteVehiculo", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use { continuation.resume(it.isSuccessful) }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Obtiene los tipos de vehículo disponibles para selección.
     */
    suspend fun getTiposVehiculo(context: Context): List<TipoVehiculo> {
        val request = authorizedRequestBuilder("$API_BASE_URL/tipos-vehiculo", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getTiposVehiculo", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            continuation.resume(json.decodeFromString<List<TipoVehiculo>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando tipos de vehiculo", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Obtiene la lista de todos los usuarios del sistema.
     *
     * @param context El contexto de la aplicación
     * @return Una lista de usuarios, o lista vacía si hay error
     */
    suspend fun getUsuarios(context: Context): List<Usuario> {
        val request = authorizedRequestBuilder("$API_BASE_URL/usuarios", context)
            .get()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en getUsuarios", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (!it.isSuccessful) {
                            Log.w("LlamadasAPI", "Error en getUsuarios: $it")
                            continuation.resume(emptyList())
                            return
                        }

                        val body = it.body?.string().orEmpty()
                        try {
                            Log.d("LlamadasAPI", "JSON recibido de usuarios: $body")
                            continuation.resume(json.decodeFromString<List<Usuario>>(body))
                        } catch (e: Exception) {
                            Log.e("LlamadasAPI", "Error parseando listado de usuarios", e)
                            Log.e("LlamadasAPI", "JSON recibido: $body", e)
                            continuation.resume(emptyList())
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuarioId El ID del usuario a actualizar
     * @param usuario El objeto Usuario con los datos actualizados
     * @param context El contexto de la aplicación
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    suspend fun updateUsuario(usuarioId: Int, usuario: Usuario, context: Context): Boolean {
        val requestBody = Json.encodeToString(usuario)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = authorizedRequestBuilder("$API_BASE_URL/usuarios/$usuarioId", context)
            .put(requestBody)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en updateUsuario", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (it.isSuccessful) {
                            Log.i("LlamadasAPI", "Usuario actualizado exitosamente")
                        } else {
                            Log.w("LlamadasAPI", "Error al actualizar usuario: $it")
                        }
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param usuarioId El ID del usuario a eliminar
     * @param context El contexto de la aplicación
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    suspend fun deleteUsuario(usuarioId: Int, context: Context): Boolean {
        val request = authorizedRequestBuilder("$API_BASE_URL/usuarios/$usuarioId", context)
            .delete()
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LlamadasAPI", "Fallo en deleteUsuario", e)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return
                    response.use {
                        if (it.isSuccessful) {
                            Log.i("LlamadasAPI", "Usuario eliminado exitosamente")
                        } else {
                            Log.w("LlamadasAPI", "Error al eliminar usuario: $it")
                        }
                        continuation.resume(it.isSuccessful)
                    }
                }
            })

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {
                }
            }
        }
    }
}
