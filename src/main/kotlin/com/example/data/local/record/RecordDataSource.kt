package com.example.data.local.record

interface RecordDataSource {
    suspend fun upsertRecordList(recordList: RecordList): Boolean

    suspend fun upsertRecord(userId: String, record: Record): Boolean
    suspend fun getRecord(userId: String, recordId: String): Record?
    suspend fun deleteRecord(userId: String, recordId: String): Boolean

    suspend fun getAllUserRecords(userId: String): List<Record>?
    suspend fun deleteAllUserRecords(userId: String): Boolean
}