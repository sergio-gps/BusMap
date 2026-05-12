package com.example.mimapa.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.R
import com.example.mimapa.data.model.MovimientoBuses
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Carga la lista de movimientos de buses desde el archivo JSON
 *
 * @param context Contexto de la aplicación
 * @return Lista de movimientos de buses
 */
fun loadMovimientoBuses(context: Context): List<MovimientoBuses> {
    val jsonFormat = Json { ignoreUnknownKeys = true }

    return try {
        val jsonString = context.resources.openRawResource(R.raw.movimiento_buses)
            .bufferedReader().use { it.readText() }
        jsonFormat.decodeFromString<List<MovimientoBuses>>(jsonString)
    } catch (e: Exception) {
        Log.e("AutoAnimatedCyclicalMarkerMap", "Error al cargar movimiento_buses.json: ${e.message}", e)
        emptyList()
    }
}

/**
 * Convierte un drawable vectorial a BitmapDescriptor para usar como icono en marcadores,
 * aplicando un color específico.
 *
 * @param context Contexto de la aplicación
 * @param vectorResId ID del recurso vectorial
 * @param hexColor Color en formato hex (ej: "#FF5733") o null para usar el color por defecto
 * @param width Ancho del icono
 * @param height Alto del icono
 * @return BitmapDescriptor para usar como icono
 */
fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    hexColor: String? = null,
    width: Int = 100,
    height: Int = 100
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    // Si se proporciona un color, aplicarlo al drawable
    if (!hexColor.isNullOrEmpty()) {
        try {
            val color = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
            drawable.mutate()
            drawable.setTint(color.toColorInt())
        } catch (e: Exception) {
            Log.w("bitmapDescriptorFromVector", "Error al aplicar color al drawable: $hexColor", e)
        }
    }

    drawable.setBounds(20, 20, width, height)
    val bm = createBitmap(width, height)
    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun AutoAnimatedCyclicalMarkerMap() {
    val positionGps = LatLng(36.83814, -2.45974)
    val context = LocalContext.current

    // --- 2. Estado de las posiciones animadas de cada bus ---
    // Mapa de posiciones: busIndex -> MutableState<LatLng>
    val markerPositionStates = remember { mutableMapOf<Int, MutableState<LatLng>>() }

    // --- Estado para los colores de las líneas ---
    // Mapa de numeroLinea -> BitmapDescriptor del icono coloreado
    val lineasIconos = remember { mutableMapOf<Int, BitmapDescriptor?>() }

    // Icono por defecto (en caso de que no se especifique color)
    var defaultBusIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    // Cantidad de autobuses que se van a mostrar en el mapa
    var numberOfBuses by remember { mutableStateOf<Int?>(null) }
    var movimientosBuses by remember { mutableStateOf<List<MovimientoBuses>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Cargar el JSON con los movimientos de buses
        movimientosBuses = loadMovimientoBuses(context)
        numberOfBuses = movimientosBuses.size

        Log.d("AutoAnimatedCyclicalMarkerMap", "Se cargaron $numberOfBuses líneas desde el JSON")

        // Crear el icono por defecto
        defaultBusIcon = bitmapDescriptorFromVector(context, R.drawable.bus_side_view, null, 100, 100)

        // Llamar a LlamadasAPI.getLinea para cada línea y crear sus iconos coloreados
        for (movimiento in movimientosBuses) {
            try {
                val linea = LlamadasAPI.getLinea(movimiento.linea, context)
                Log.d("AutoAnimatedCyclicalMarkerMap", "Línea obtenida: $linea")

                // Convertir el color de la línea y crear el icono coloreado
                if (linea != null && linea.color != null) {
                    val icono = bitmapDescriptorFromVector(
                        context,
                        R.drawable.bus_side_view,
                        linea.color,
                        100,
                        100
                    )
                    lineasIconos[movimiento.linea] = icono
                    Log.d("AutoAnimatedCyclicalMarkerMap", "Icono creado para línea ${movimiento.linea} con color: ${linea.color}")
                } else {
                    lineasIconos[movimiento.linea] = defaultBusIcon
                }
            } catch (e: Exception) {
                Log.e("AutoAnimatedCyclicalMarkerMap", "Error al obtener la línea ${movimiento.linea}: ${e.message}", e)
                lineasIconos[movimiento.linea] = defaultBusIcon
            }
        }
    }

    // --- 4. Efecto automático: Inicializar y animar todos los buses en paralelo ---
    LaunchedEffect(movimientosBuses) { // Se ejecuta cuando cambia movimientosBuses
        // Si no hay movimientos de buses, no continuar
        if (movimientosBuses.isEmpty()) {
            Log.w("AutoAnimatedCyclicalMarkerMap", "No hay movimientos de buses para animar")
            return@LaunchedEffect
        }

        // Limpiar estados anteriores y crear nuevos para cada bus
        markerPositionStates.clear()
        movimientosBuses.forEachIndexed { index, movimiento ->
            val initialPosition = movimiento.ruta.firstOrNull()?.toGoogleMapsLatLng()
                ?: LatLng(36.83936125117198, -2.459356635586987)
            markerPositionStates[index] = mutableStateOf(initialPosition)
        }

        val animationSpec = tween<Float>(durationMillis = 2000) // 2 seg. por tramo

        // Lanzar una corrutina animadora para CADA bus
        movimientosBuses.forEachIndexed { busIndex, movimiento ->
            launch {
                val routePoints = movimiento.ruta.map { it.toGoogleMapsLatLng() }

                if (routePoints.isEmpty()) {
                    Log.w("AutoAnimatedCyclicalMarkerMap", "La línea ${movimiento.linea} no tiene puntos de ruta")
                    return@launch
                }

                // Cada bus itera infinitamente a través de su propia ruta (ciclo continuo)
                while (true) {
                    for (i in routePoints.indices) {
                        // Punto de inicio = posición actual del bus
                        val startPosition = markerPositionStates[busIndex]?.value ?: routePoints[i]

                        // Punto de destino = el siguiente punto en la lista (cuando acaba vuelve al inicio)
                        val targetPosition = if (i == routePoints.lastIndex) {
                            routePoints[0] // Vuelve al primer punto
                        } else {
                            routePoints[i + 1]
                        }

                        // Crear un animador independiente para este segmento
                        val segmentFraction = Animatable(0f)

                        // Animar el segmento
                        segmentFraction.animateTo(
                            targetValue = 1f,
                            animationSpec = animationSpec
                        ) {
                            // En cada frame, calcula la posición intermedia
                            val interpolatedPosition = SphericalUtil.interpolate(
                                startPosition,
                                targetPosition,
                                value.toDouble()
                            )
                            // Actualiza la posición de este bus específico
                            markerPositionStates[busIndex]?.value = interpolatedPosition
                        }

                        // (Opcional) Una pequeña pausa antes de empezar el siguiente tramo
                        delay(500)
                    }
                }
            }
        }
    }

    // --- 5. La UI del Mapa ---
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                // Centra la cámara en el primer punto de la primera línea si existe
                val firstPoint = movimientosBuses.firstOrNull()?.ruta?.firstOrNull()?.toGoogleMapsLatLng()
                    ?: LatLng(36.83936125117198, -2.459356635586987)
                position = CameraPosition.fromLatLngZoom(firstPoint, 17f) // Zoom más cercano
            }
        ) {
            Marker(
                state = remember { MarkerState(position = positionGps) },
                title = "One Marker"
            )
            
            // Renderizar un marcador para cada bus en su posición actual
            markerPositionStates.forEach { (busIndex, positionState) ->
                val numeroLinea = movimientosBuses.getOrNull(busIndex)?.linea
                val busIcono = if (numeroLinea != null) {
                    lineasIconos[numeroLinea] ?: defaultBusIcon
                } else {
                    defaultBusIcon
                }

                Marker(
                    state = MarkerState(position = positionState.value),
                    title = "Bus Línea $numeroLinea",
                    icon = busIcono ?: BitmapDescriptorFactory.defaultMarker()
                )
            }
        }
    }
}