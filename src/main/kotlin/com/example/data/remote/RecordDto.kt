package com.example.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RecordDto(
    val id: String,
    val isChecked: Boolean,
    val name: String,
    val description: String,
    val isTask: Boolean,
    //val urgency: String,
    val deadline: Long,
    //val tags: List<String>
)