package com.example.mimapa.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.intOrNull

/**
 * Representa una parada de autobús del fichero paradas.json.
 *
 * @param numero El número identificador de la parada.
 * @param nombre El nombre de la parada.
 * @param latitude La coordenada de latitud.
 * @param longitude La coordenada de longitud.
 * @param lineas Lista de las líneas de autobús que pasan por esta parada.
 */
@Serializable
data class Parada(
    val numero: Int,
    val nombre: String,
    val latitude: Double,
    val longitude: Double,
    @Serializable(with = LineasIntSerializer::class)
    val lineas: List<Int>
)

/**
 * Permite decodificar el campo lineas cuando llega como enteros o como strings numéricos.
 */
object LineasIntSerializer : kotlinx.serialization.KSerializer<List<Int>> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("LineasInt", kotlinx.serialization.descriptors.StructureKind.LIST)

    override fun deserialize(decoder: Decoder): List<Int> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("LineasIntSerializer solo soporta JSON")

        val element = jsonDecoder.decodeJsonElement()
        val array = element as? JsonArray
            ?: throw SerializationException("El campo lineas debe ser un array JSON")

        return array.mapNotNull { item: JsonElement ->
            val primitive = item as? JsonPrimitive ?: return@mapNotNull null
            primitive.intOrNull ?: primitive.content.toIntOrNull()
        }
    }

    override fun serialize(encoder: Encoder, value: List<Int>) {
        encoder.encodeSerializableValue(
            kotlinx.serialization.builtins.ListSerializer(kotlinx.serialization.builtins.IntSerializer()),
            value
        )
    }
}
