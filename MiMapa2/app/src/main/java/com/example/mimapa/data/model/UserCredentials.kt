package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val username: String, val password: String)