package com.example.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: String,
    val name: String
)
