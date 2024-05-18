package com.example.data.remote.responses

import com.example.data.remote.RecordDto
import kotlinx.serialization.Serializable

@Serializable
data class RecordsResponse(
    val records: List<RecordDto>
)
