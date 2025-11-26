package com.example.mimapa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.BuildConfig.MAPS_API_KEY
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Pantalla de búsqueda de ubicación utilizando Google Places API.
 * 
 * Permite al usuario buscar ubicaciones mediante un campo de texto con autocompletado
 * en tiempo real. Al seleccionar un lugar, las coordenadas se pasan a la pantalla anterior
 * a través del NavController.
 *
 * @param navController Controlador de navegación para retornar a la pantalla anterior
 * con las coordenadas seleccionadas.
 */
@Composable
fun FindLocationScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inicializar Places API con la nueva versión (5.0.0+)
    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, MAPS_API_KEY)
        }
        Places.createClient(context)
    }

    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    /**
     * Busca lugares mediante Google Places API con debounce.
     * 
     * Cancela la búsqueda anterior si existe y espera 300ms antes de ejecutar
     * la nueva búsqueda para evitar llamadas excesivas a la API.
     *
     * @param query Texto de búsqueda introducido por el usuario.
     * Requiere mínimo 3 caracteres para realizar la búsqueda.
     */
    fun searchPlaces(query: String) {
        searchJob?.cancel()
        
        if (query.length < 3) {
            predictions = emptyList()
            return
        }

        searchJob = scope.launch {
            delay(300) // Debounce de 300ms para optimizar llamadas a la API
            isLoading = true
            try {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(sessionToken)
                    .setQuery(query)
                    .build()

                val response = placesClient.findAutocompletePredictions(request).await()
                
                predictions = response.autocompletePredictions.map { prediction ->
                    AutocompletePrediction(
                        placeId = prediction.placeId,
                        primaryText = prediction.getPrimaryText(null).toString(),
                        secondaryText = prediction.getSecondaryText(null).toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                predictions = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Obtiene los detalles completos de un lugar seleccionado.
     * 
     * Realiza una petición a Places API para obtener las coordenadas del lugar
     * y las pasa a la pantalla anterior mediante NavController.
     *
     * @param placeId ID único del lugar obtenido de las predicciones.
     */
    fun selectPlace(placeId: String) {
        scope.launch {
            isLoading = true
            try {
                val placeFields = listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.LOCATION
                )

                val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                val response = placesClient.fetchPlace(request).await()
                val place = response.place

                val coordinates = place.location?.let { 
                    LatLng(it.latitude, it.longitude) 
                }

                // Pasar las coordenadas y el nombre a la pantalla anterior y cerrar esta pantalla
                coordinates?.let {
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("selected_location", it)
                        set("selected_location_name", place.displayName)
                    }
                    navController.popBackStack()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
                searchPlaces(newValue)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar ubicación...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        predictions = emptyList()
                        searchJob?.cancel()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(predictions) { prediction ->
                PredictionCard(
                    prediction = prediction,
                    onClick = { selectPlace(prediction.placeId) }
                )
            }
        }

        if (searchQuery.isNotEmpty() && predictions.isEmpty() && !isLoading) {
            Text(
                text = "No se encontraron resultados",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjeta que muestra una predicción de ubicación en la lista de resultados.
 * 
 * Presenta el nombre principal del lugar y su descripción secundaria
 * (generalmente la dirección o región) en un formato de tarjeta clickeable.
 *
 * @param prediction Datos de la predicción obtenida de Places API.
 * @param onClick Callback que se ejecuta cuando el usuario selecciona esta predicción.
 */
@Composable
private fun PredictionCard(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = prediction.primaryText,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = prediction.secondaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Modelo de datos para representar una predicción de autocompletado.
 * 
 * Contiene la información básica de un lugar obtenido de Google Places API
 * necesaria para mostrar en la lista de sugerencias.
 *
 * @property placeId Identificador único del lugar en Places API.
 * @property primaryText Texto principal (nombre del lugar).
 * @property secondaryText Texto secundario (dirección o región).
 */
private data class AutocompletePrediction(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String
)
