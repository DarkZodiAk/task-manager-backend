package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.local.user.UserDataSource
import com.sandbox.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity(
    config: TokenConfig,
    userDataSource: UserDataSource
) {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtRealm = "ktor sample app"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    val principal = JWTPrincipal(credential.payload)
                    val userId = principal.getClaim("userId", String::class) ?: return@validate null
                    if (userDataSource.getUserById(userId)?.isLoggedIn == true){
                        principal
                    } else null
                } else null
            }
        }
    }
}
