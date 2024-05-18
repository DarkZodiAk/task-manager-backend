package com.example

import com.example.data.local.tag.TagDataSource
import com.example.data.mappers.toTag
import com.example.data.mappers.toTagDto
import com.example.data.remote.dto.TagDto
import com.example.data.remote.responses.TagsResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.upsertTag(
    tagDataSource: TagDataSource
) {
    authenticate {
        post("upsert_tag") {
            val request = call.receiveNullable<TagDto>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@post
            }

            val wasAcknowledged = tagDataSource.upsertTag(
                userId = userId,
                tag = request.toTag()
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.InternalServerError)
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.tags(
    tagDataSource: TagDataSource
) {
    authenticate {
        get("tags") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            val tags = tagDataSource.getAllUserTags(userId) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = TagsResponse(tags.map { it.toTagDto() })
            )
        }
    }
}

fun Route.delete_tag(
    tagDataSource: TagDataSource
) {
    authenticate {
        delete("delete_tag/{id}") {
            val tagId = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@delete
            }

            val wasAcknowledged = tagDataSource.deleteTag(userId, tagId)
            if(!wasAcknowledged){
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}