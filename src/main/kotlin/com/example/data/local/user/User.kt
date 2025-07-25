package com.example.data.local.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId val id: ObjectId = ObjectId(),
    val email: String,
    val username: String,
    val password: String,
    val salt: String,
    val isLoggedIn: Boolean = false
)
