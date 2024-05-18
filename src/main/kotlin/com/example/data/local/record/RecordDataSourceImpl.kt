package com.example.data.local.record

import com.mongodb.client.model.UpdateOptions
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class RecordDataSourceImpl(
    private val db: CoroutineDatabase
): RecordDataSource {
    private val records = db.getCollection<RecordList>("records")

    override suspend fun upsertRecordList(recordList: RecordList): Boolean {
        return records.updateOne(
            filter = RecordList::id eq recordList.id,
            target = recordList,
            options = UpdateOptions().upsert(true)
        ).wasAcknowledged()
    }

    override suspend fun upsertRecord(userId: String, record: Record): Boolean {
        val list = getAllUserRecords(userId)?.toMutableList() ?: return false
        val index = list.indexOfFirst { it.id == record.id }
        if(index == -1){
            list.add(record)
        } else {
            list[index] = record
        }
        return upsertRecordList(
            RecordList(userId, list)
        )
    }

    override suspend fun getRecord(userId: String, recordId: String): Record? {
        val list = getAllUserRecords(userId) ?: return null
        return list.find { it.id == ObjectId(recordId) }
    }

    override suspend fun getAllUserRecords(userId: String): List<Record>? {
        return records.findOne(RecordList::id eq ObjectId(userId))?.records
    }

    override suspend fun deleteRecord(userId: String, recordId: String): Boolean {
        val list = getAllUserRecords(userId)?.toMutableList() ?: return false
        list.removeIf { it.id == ObjectId(recordId) }
        return upsertRecordList(
            RecordList(userId, list)
        )
    }

    override suspend fun deleteAllUserRecords(userId: String): Boolean {
        return records.deleteOne(RecordList::id eq ObjectId(userId)).wasAcknowledged()
    }
}