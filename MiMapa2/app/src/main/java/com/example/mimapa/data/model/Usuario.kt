package com.example.mimapa.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

// Serializer personalizado para manejar roles que pueden ser strings o objetos
object RoleListSerializer : KSerializer<List<Rol>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RoleList")

    override fun deserialize(decoder: Decoder): List<Rol> {
        val jsonDecoder = decoder as? JsonDecoder ?: return emptyList()
        val jsonElement = jsonDecoder.decodeJsonElement()

        return when {
            jsonElement is kotlinx.serialization.json.JsonArray -> {
                jsonElement.mapNotNull { element ->
                    when (element) {
                        is JsonPrimitive -> {
                            // Si es un string, crear un Rol con el nombre
                            val rolName = element.content
                            Rol(rolId = 0, rolName = rolName)
                        }
                        is JsonObject -> {
                            // Si es un objeto, deserializarlo normalmente
                            try {
                                val rolId = element["rolId"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                                val rolName = element["rolName"]?.jsonPrimitive?.content ?: ""
                                Rol(rolId = rolId, rolName = rolName)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        else -> null
                    }
                }
            }
            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<Rol>) {
        val listSerializer = ListSerializer(Rol.serializer())
        listSerializer.serialize(encoder, value)
    }
}

/**
 * Representa un usuario del sistema BusMap.
 *
 * @param usuarioId Identificador único del usuario (mapeado desde 'usuarioId' en el JSON)
 * @param email Correo electrónico del usuario (único)
 * @param username Nombre de usuario (puede ser null)
 * @param roles Lista de roles asignados al usuario
 */
@Serializable
data class Usuario(
    @SerialName("usuarioId")
    val usuarioId: Int,
    val email: String,
    val username: String? = null,
    @Serializable(with = RoleListSerializer::class)
    val roles: List<Rol> = emptyList()
)
