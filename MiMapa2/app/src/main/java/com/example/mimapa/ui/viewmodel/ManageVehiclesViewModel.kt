package com.example.mimapa.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.data.model.TipoVehiculo
import com.example.mimapa.data.model.VehiculoAdmin
import com.example.mimapa.data.model.VehiculoAdminRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageVehiclesViewModel : ViewModel() {

    var vehiculosList by mutableStateOf<List<VehiculoAdmin>>(emptyList())
        private set

    var filteredVehiculos by mutableStateOf<List<VehiculoAdmin>>(emptyList())
        private set

    var tiposVehiculo by mutableStateOf<List<TipoVehiculo>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    fun loadInitialData(context: Context) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                refreshVehiculos(context)
                tiposVehiculo = LlamadasAPI.getTiposVehiculo(context).sortedBy { it.nombre }
            } catch (e: Exception) {
                errorMessage = "Error cargando vehículos: ${e.message}"
                vehiculosList = emptyList()
                filteredVehiculos = emptyList()
                tiposVehiculo = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun refreshVehiculos(context: Context) {
        vehiculosList = if (searchQuery.isBlank()) {
            LlamadasAPI.getVehiculos(context)
        } else {
            LlamadasAPI.searchVehiculos(searchQuery, context)
        }.sortedBy { it.vehiculoId ?: Int.MAX_VALUE }

        filteredVehiculos = vehiculosList
    }

    fun updateSearchQuery(query: String, context: Context) {
        searchQuery = query
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            try {
                refreshVehiculos(context)
            } catch (_: Exception) {
            }
        }
    }

    fun createVehiculo(
        request: VehiculoAdminRequest,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val created = LlamadasAPI.createVehiculo(request, context)
                if (created != null) {
                    refreshVehiculos(context)
                    tiposVehiculo = LlamadasAPI.getTiposVehiculo(context).sortedBy { it.nombre }
                    onSuccess()
                } else {
                    onError("No se pudo crear el vehículo")
                }
            } catch (e: Exception) {
                onError("Error al crear vehículo: ${e.message}")
            }
        }
    }

    fun updateVehiculo(
        vehiculoId: Int,
        request: VehiculoAdminRequest,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val updated = LlamadasAPI.updateVehiculo(vehiculoId, request, context)
                if (updated != null) {
                    refreshVehiculos(context)
                    tiposVehiculo = LlamadasAPI.getTiposVehiculo(context).sortedBy { it.nombre }
                    onSuccess()
                } else {
                    onError("No se pudo actualizar el vehículo")
                }
            } catch (e: Exception) {
                onError("Error al actualizar vehículo: ${e.message}")
            }
        }
    }

    fun deleteVehiculo(
        vehiculoId: Int,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val ok = LlamadasAPI.deleteVehiculo(vehiculoId, context)
                if (ok) {
                    refreshVehiculos(context)
                    tiposVehiculo = LlamadasAPI.getTiposVehiculo(context).sortedBy { it.nombre }
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el vehículo")
                }
            } catch (e: Exception) {
                onError("Error al eliminar vehículo: ${e.message}")
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
