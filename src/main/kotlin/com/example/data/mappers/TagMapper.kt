package com.example.data.mappers

import com.example.data.local.tag.Tag
import com.example.data.remote.dto.TagDto
import org.bson.types.ObjectId

fun Tag.toTagDto(): TagDto {
    return TagDto(
        id.toString(), name
    )
}

fun TagDto.toTag(): Tag {
    return Tag(
        ObjectId(id), name
    )
}