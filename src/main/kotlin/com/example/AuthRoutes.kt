package com.example

import com.example.data.local.record.RecordDataSource
import com.example.data.local.record.RecordList
import com.example.data.local.user.User
import com.example.data.local.user.UserDataSource
import com.example.data.remote.requests.LoginRequest
import com.example.data.remote.requests.RegisterRequest
import com.example.data.remote.responses.LoginResponse
import com.sandbox.security.hashing.HashingService
import com.sandbox.security.hashing.SaltedHash
import com.sandbox.security.token.TokenClaim
import com.sandbox.security.token.TokenConfig
import com.sandbox.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    recordDataSource: RecordDataSource
) {
    post("signup") {
        val request = call.receiveNullable<RegisterRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        var wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged) {
            call.respond(HttpStatusCode.InternalServerError)
            return@post
        }

        wasAcknowledged = recordDataSource.upsertRecordList(RecordList(user.id))
        if(!wasAcknowledged) {
            call.respond(HttpStatusCode.InternalServerError)
            userDataSource.deleteUserById(user.id.toString())
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if(user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if(!isValidPassword) {
            call.respond(HttpStatusCode.BadRequest, "Incorrect password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        val wasAcknowledged = userDataSource.updateUser(
            user.copy(isLoggedIn = true)
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = LoginResponse(
                token = token,
                username = user.username
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.logout(
    userDataSource: UserDataSource
) {
    authenticate {
        get("logout") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            val user = userDataSource.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            val wasAcknowledged = userDataSource.updateUser(
                user.copy(isLoggedIn = false)
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}