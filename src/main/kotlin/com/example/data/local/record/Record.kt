package com.example.data.local.record

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class RecordList(
    @BsonId val id: ObjectId,
    val records: List<Record> = emptyList()
) {
    constructor(id: String, records: List<Record>): this(ObjectId(id), records)
    constructor(id: String): this(ObjectId(id))
}

data class Record(
    @BsonId val id: ObjectId = ObjectId(),
    val isChecked: Boolean,
    val name: String,
    val description: String,
    val isTask: Boolean,
    val deadline: Long,
)