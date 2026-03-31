package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val email: String, val password: String)