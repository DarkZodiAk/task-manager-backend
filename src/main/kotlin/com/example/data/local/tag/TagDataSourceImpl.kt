package com.example.data.local.tag

import com.mongodb.client.model.UpdateOptions
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class TagDataSourceImpl(
    db: CoroutineDatabase
): TagDataSource {
    private val tags = db.getCollection<TagList>("tags")

    override suspend fun upsertTagList(tagList: TagList): Boolean {
        return tags.updateOne(
            filter = TagList::id eq tagList.id,
            target = tagList,
            options = UpdateOptions().upsert(true)
        ).wasAcknowledged()
    }

    override suspend fun upsertTag(userId: String, tag: Tag): Boolean {
        val list = getAllUserTags(userId)?.toMutableList() ?: return false
        val index = list.indexOfFirst { it.id == tag.id }
        if (index == -1) {
            list.add(tag)
        } else {
            list[index] = tag
        }
        return upsertTagList(
            TagList(userId, list)
        )
    }

    override suspend fun getTag(userId: String, tagId: String): Tag? {
        val list = getAllUserTags(userId) ?: return null
        return list.find { it.id == ObjectId(tagId) }
    }

    override suspend fun getAllUserTags(userId: String): List<Tag>? {
        return tags.findOne(TagList::id eq ObjectId(userId))?.tags
    }

    override suspend fun deleteTag(userId: String, tagId: String): Boolean {
        val list = getAllUserTags(userId)?.toMutableList() ?: return false
        list.removeIf { it.id == ObjectId(tagId) }
        return upsertTagList(
            TagList(userId, list)
        )
    }

    override suspend fun deleteAllUserTags(userId: String): Boolean {
        return tags.deleteOne(TagList::id eq ObjectId(userId)).wasAcknowledged()
    }
}