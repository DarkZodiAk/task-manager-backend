package com.example.data.local

import com.example.data.local.record.Record
import com.example.data.remote.RecordDto
import org.bson.types.ObjectId

fun Record.toRecordDto(): RecordDto {
    return RecordDto(
        id.toString(), isChecked, name, description, isTask, /*urgency,*/ deadline/*, tags*/
    )
}

fun RecordDto.toRecord(): Record {
    return Record(
        ObjectId(id), isChecked, name, description, isTask, /*urgency,*/ deadline/*, tags*/
    )
}