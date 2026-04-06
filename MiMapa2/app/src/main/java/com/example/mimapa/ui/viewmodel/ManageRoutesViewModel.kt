package com.example.mimapa.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.data.model.Linea
import com.example.mimapa.data.model.Parada
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageRoutesViewModel : ViewModel() {

    var lineasList by mutableStateOf<List<Linea>>(emptyList())
        private set

    var filteredLineas by mutableStateOf<List<Linea>>(emptyList())
        private set

    var paradasList by mutableStateOf<List<Parada>>(emptyList())
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
                refreshLineas(context)
                paradasList = LlamadasAPI.getParadas(context).sortedBy { it.numero }
            } catch (e: Exception) {
                errorMessage = "Error cargando datos: ${e.message}"
                lineasList = emptyList()
                filteredLineas = emptyList()
                paradasList = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun refreshLineas(context: Context) {
        lineasList = LlamadasAPI.getLineas(context).sortedBy { it.lineaId }
        filteredLineas = if (searchQuery.isBlank()) {
            lineasList
        } else {
            val q = searchQuery.trim().lowercase()
            lineasList.filter { linea ->
                linea.nombre.lowercase().contains(q) || linea.lineaId.toString().contains(q)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchJob?.cancel()

        if (query.isBlank()) {
            filteredLineas = lineasList
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            val q = query.trim().lowercase()
            filteredLineas = lineasList.filter { linea ->
                linea.nombre.lowercase().contains(q) || linea.lineaId.toString().contains(q)
            }
        }
    }

    fun createLinea(linea: Linea, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val created = LlamadasAPI.createLinea(linea, context)
                if (created != null) {
                    refreshLineas(context)
                    onSuccess()
                } else {
                    onError("No se pudo crear la línea")
                }
            } catch (e: Exception) {
                onError("Error al crear línea: ${e.message}")
            }
        }
    }

    fun updateLinea(lineaId: Int, linea: Linea, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val updated = LlamadasAPI.updateLinea(lineaId, linea, context)
                if (updated != null) {
                    refreshLineas(context)
                    onSuccess()
                } else {
                    onError("No se pudo actualizar la línea")
                }
            } catch (e: Exception) {
                onError("Error al actualizar línea: ${e.message}")
            }
        }
    }

    fun deleteLinea(lineaId: Int, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val ok = LlamadasAPI.deleteLinea(lineaId, context)
                if (ok) {
                    refreshLineas(context)
                    onSuccess()
                } else {
                    onError("No se pudo eliminar la línea")
                }
            } catch (e: Exception) {
                onError("Error al eliminar línea: ${e.message}")
            }
        }
    }

    fun addParadaToLinea(lineaId: Int, paradaId: Int, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val ok = LlamadasAPI.addParadaToLinea(lineaId, paradaId, context)
                if (ok) {
                    refreshLineas(context)
                    onSuccess()
                } else {
                    onError("No se pudo añadir la parada")
                }
            } catch (e: Exception) {
                onError("Error añadiendo parada: ${e.message}")
            }
        }
    }

    fun removeParadaFromLinea(lineaId: Int, paradaId: Int, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val ok = LlamadasAPI.removeParadaFromLinea(lineaId, paradaId, context)
                if (ok) {
                    refreshLineas(context)
                    onSuccess()
                } else {
                    onError("No se pudo quitar la parada")
                }
            } catch (e: Exception) {
                onError("Error quitando parada: ${e.message}")
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
