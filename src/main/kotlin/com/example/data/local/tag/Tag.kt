package com.example.data.local.tag

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TagList(
    @BsonId val id: ObjectId,
    val tags: List<Tag> = emptyList()
) {
    constructor(id: String, tags: List<Tag>): this(ObjectId(id), tags)
    constructor(id: String): this(ObjectId(id))
}

data class Tag(
    @BsonId val id: ObjectId = ObjectId(),
    val name: String
)
