package com.example.mimapa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Passwords(val currentPassword: String, val newPassword: String)