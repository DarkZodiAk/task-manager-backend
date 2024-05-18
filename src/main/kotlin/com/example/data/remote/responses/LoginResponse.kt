package com.example.data.remote.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val username: String
)
