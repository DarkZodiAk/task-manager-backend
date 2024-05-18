package com.example.data.local.tag


interface TagDataSource {
    suspend fun upsertTagList(tagList: TagList): Boolean

    suspend fun upsertTag(userId: String, tag: Tag): Boolean
    suspend fun getTag(userId: String, tagId: String): Tag?
    suspend fun deleteTag(userId: String, tagId: String): Boolean

    suspend fun getAllUserTags(userId: String): List<Tag>?
    suspend fun deleteAllUserTags(userId: String): Boolean
}