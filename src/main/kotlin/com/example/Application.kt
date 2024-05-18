package com.example

import com.example.data.local.record.RecordDataSourceImpl
import com.example.data.local.tag.TagDataSourceImpl
import com.example.data.local.user.UserDataSourceImpl
import com.example.plugins.*
import com.sandbox.security.hashing.SHA256HashingService
import com.sandbox.security.token.JWTTokenService
import com.sandbox.security.token.TokenConfig
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val dbName = "taskManager"
    val db = KMongo.createClient("mongodb://localhost:27017")
        .coroutine.getDatabase(dbName)

    val userDataSource = UserDataSourceImpl(db)
    val recordDataSource = RecordDataSourceImpl(db)
    val tagDataSource = TagDataSourceImpl(db)
    val tokenService = JWTTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig, userDataSource)
    configureRouting(userDataSource, recordDataSource, tagDataSource, hashingService, tokenService, tokenConfig)
}
