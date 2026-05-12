package com.example.mimapa.util

/**
 * Utilidad para trabajar con información de consumo de combustible.
 */
object FuelConsumptionHelper {

    private const val DIESEL_CO2_KG_PER_LITER = 2.6391

    /**
     * Convierte microlitros a litros.
     *
     * @param microliters Consumo en microlitros (como String o Long)
     * @return Consumo en litros con 2 decimales
     */
    fun microlitersToLiters(microliters: String?): Double {
        return if (microliters.isNullOrEmpty()) {
            0.0
        } else {
            try {
                microliters.toLong() / 1_000_000.0
            } catch (e: NumberFormatException) {
                0.0
            }
        }
    }

    /**
     * Formatea el consumo de combustible para mostrar al usuario.
     *
     * @param microliters Consumo en microlitros
     * @param distanceMeters Distancia total en metros (opcional, para calcular consumo por km)
     * @return Cadena formateada, ej: "11.02 L" o "7.9 L/100km"
     */
    fun formatFuelConsumption(microliters: String?, distanceMeters: Int? = null): String {
        val liters = microlitersToLiters(microliters)
        if (liters == 0.0) return "N/A"

        return if (distanceMeters != null && distanceMeters > 0) {
            // Calcular L/100km: (litros / distancia_km) * 100
            val km = distanceMeters / 1000.0
            val litersPer100km = (liters / km) * 100
            "%.1f L/100km (%.2f L)".format(litersPer100km, liters)
        } else {
            "%.2f L".format(liters)
        }
    }

    /**
     * Obtiene una descripción del consumo según el tipo de combustible.
     *
     * @param microliters Consumo en microlitros
     * @param emissionType Tipo de emisión: GASOLINE, DIESEL, ELECTRIC, HYBRID
     * @return Descripción del consumo
     */
    fun getConsumptionDescription(microliters: String?, emissionType: String = "GASOLINE"): String {
        val liters = microlitersToLiters(microliters)
        if (liters == 0.0) return "Sin datos de consumo"

        return when (emissionType.uppercase()) {
            "GASOLINE" -> "Consumo de gasolina: %.2f litros".format(liters)
            "DIESEL" -> "Consumo de diésel: %.2f litros".format(liters)
            "ELECTRIC" -> "Consumo equivalente: %.2f litros".format(liters)
            "HYBRID" -> "Consumo híbrido: %.2f litros equivalentes".format(liters)
            else -> "Consumo: %.2f litros".format(liters)
        }
    }

    /**
     * Calcula el CO2 emitido para diésel en kg usando el factor:
     * 2.6391 kg de CO2 por litro consumido.
     *
     * @param microliters Consumo en microlitros.
     * @return CO2 emitido en kg.
     */
    fun calculateDieselCo2Kg(microliters: String?): Double {
        val liters = microlitersToLiters(microliters)
        if (liters <= 0.0) {
            return 0.0
        }
        return liters * DIESEL_CO2_KG_PER_LITER
    }

    /**
     * Formatea el CO2 emitido en kg para mostrar al usuario.
     *
     * @param co2Kg CO2 emitido en kg.
     * @return Texto formateado en kg o N/A si no hay dato válido.
     */
    fun formatDieselCo2Kg(co2Kg: Double?): String {
        return if (co2Kg == null || co2Kg <= 0.0) {
            "N/A"
        } else {
            "%.2f kg".format(co2Kg)
        }
    }
}
