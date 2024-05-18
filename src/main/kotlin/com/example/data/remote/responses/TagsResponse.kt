package com.example.data.remote.responses

import com.example.data.remote.dto.TagDto
import kotlinx.serialization.Serializable

@Serializable
data class TagsResponse(
    val tags: List<TagDto>
)
