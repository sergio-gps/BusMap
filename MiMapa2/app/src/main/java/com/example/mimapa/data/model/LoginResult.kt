package com.example.mimapa.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val token: String? = null,
    @SerialName("rol")
    val role: List<String>? = null
)
