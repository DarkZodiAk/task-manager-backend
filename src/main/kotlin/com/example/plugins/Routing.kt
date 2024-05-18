package com.example.plugins

import com.example.*
import com.example.data.local.record.RecordDataSource
import com.example.data.local.user.UserDataSource
import com.sandbox.security.hashing.HashingService
import com.sandbox.security.token.TokenConfig
import com.sandbox.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    recordDataSource: RecordDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource, recordDataSource)
        authenticate()
        logout(userDataSource)

        upsertRecord(recordDataSource)
        records(recordDataSource)
        delete_record(recordDataSource)
    }
}
