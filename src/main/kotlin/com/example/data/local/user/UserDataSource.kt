package com.example.data.local.user

interface UserDataSource {
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: String): User?
    suspend fun deleteUserById(id: String): Boolean
}