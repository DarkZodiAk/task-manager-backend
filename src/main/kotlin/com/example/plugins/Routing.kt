package com.example.plugins

import com.example.*
import com.example.data.local.record.RecordDataSource
import com.example.data.local.tag.TagDataSource
import com.example.data.local.user.UserDataSource
import com.sandbox.security.hashing.HashingService
import com.sandbox.security.token.TokenConfig
import com.sandbox.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    recordDataSource: RecordDataSource,
    tagDataSource: TagDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource, recordDataSource, tagDataSource)
        authenticate()
        getSecretInfo()
        logout(userDataSource)
        deleteUser(userDataSource, recordDataSource, tagDataSource)

        upsertRecord(recordDataSource)
        records(recordDataSource)
        delete_record(recordDataSource)

        upsertTag(tagDataSource)
        tags(tagDataSource)
        delete_tag(tagDataSource)
    }
}
