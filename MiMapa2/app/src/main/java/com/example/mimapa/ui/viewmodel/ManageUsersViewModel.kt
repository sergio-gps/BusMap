package com.example.mimapa.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimapa.LlamadasAPI
import com.example.mimapa.data.model.Usuario
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de gestión de usuarios (ManageUsersScreen).
 * Proporciona funcionalidad para listar, buscar, actualizar y eliminar usuarios.
 */
class ManageUsersViewModel : ViewModel() {

    // Estado de la lista de usuarios completa
    var usuariosList by mutableStateOf<List<Usuario>>(emptyList())
        private set

    // Estado de búsqueda
    var searchQuery by mutableStateOf("")
        private set

    // Usuarios filtrados según la búsqueda
    var filteredUsuarios by mutableStateOf<List<Usuario>>(emptyList())
        private set

    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set

    // Mensaje de error
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Job para el debounce de búsqueda
    private var searchJob: Job? = null

    // Debounce delay en milisegundos
    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    /**
     * Carga la lista de todos los usuarios desde el servidor.
     *
     * @param context El contexto de la aplicación
     */
    fun loadUsuarios(context: Context) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                usuariosList = LlamadasAPI.getUsuarios(context)
                filteredUsuarios = usuariosList
            } catch (e: Exception) {
                errorMessage = "Error al cargar usuarios: ${e.message}"
                usuariosList = emptyList()
                filteredUsuarios = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Actualiza la consulta de búsqueda con debounce.
     * Filtra usuarios por nombre o email que coincidan con la consulta.
     *
     * @param query La consulta de búsqueda
     */
    fun updateSearchQuery(query: String) {
        searchQuery = query
        
        // Cancelar el job anterior si existe
        searchJob?.cancel()
        
        // Si la consulta está vacía, mostrar todos los usuarios
        if (query.isBlank()) {
            filteredUsuarios = usuariosList
            return
        }
        
        // Crear un nuevo job con debounce
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            
            val queryLower = query.lowercase()
            filteredUsuarios = usuariosList.filter { usuario ->
                usuario.username?.lowercase()?.contains(queryLower) == true ||
                usuario.email.lowercase().contains(queryLower)
            }
        }
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param usuarioId El ID del usuario a actualizar
     * @param usuario El objeto Usuario con los datos actualizados
     * @param context El contexto de la aplicación
     * @param onSuccess Callback ejecutado si la actualización fue exitosa
     * @param onError Callback ejecutado si hay error
     */
    fun updateUsuario(
        usuarioId: Int,
        usuario: Usuario,
        context: Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val success = LlamadasAPI.updateUsuario(usuarioId, usuario, context)
                if (success) {
                    // Actualizar la lista local
                    val index = usuariosList.indexOfFirst { it.usuarioId == usuarioId }
                    if (index >= 0) {
                        val updatedList = usuariosList.toMutableList()
                        updatedList[index] = usuario
                        usuariosList = updatedList
                        // Replicar el cambio en la lista filtrada si es necesario
                        updateSearchQuery(searchQuery)
                    }
                    onSuccess()
                } else {
                    onError("Error al actualizar usuario")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param usuarioId El ID del usuario a eliminar
     * @param context El contexto de la aplicación
     * @param onSuccess Callback ejecutado si la eliminación fue exitosa
     * @param onError Callback ejecutado si hay error
     */
    fun deleteUsuario(
        usuarioId: Int,
        context: Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val success = LlamadasAPI.deleteUsuario(usuarioId, context)
                if (success) {
                    // Remover de la lista local
                    usuariosList = usuariosList.filter { it.usuarioId != usuarioId }
                    // Replicar el cambio en la lista filtrada
                    updateSearchQuery(searchQuery)
                    onSuccess()
                } else {
                    onError("Error al eliminar usuario")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        errorMessage = null
    }
}
