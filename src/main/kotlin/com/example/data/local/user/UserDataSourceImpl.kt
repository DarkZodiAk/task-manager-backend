package com.example.data.local.user

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(
    private val db: CoroutineDatabase
): UserDataSource {
    private val users = db.getCollection<User>("users")

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun updateUser(user: User): Boolean {
        return users.updateOne(
            filter = User::id eq user.id,
            target = user
        ).wasAcknowledged()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOne(User::id eq ObjectId(id))
    }

    override suspend fun deleteUserById(id: String): Boolean {
        return users.deleteOne(User::id eq ObjectId(id)).wasAcknowledged()
    }
}