package com.example

import com.example.data.local.record.RecordDataSource
import com.example.data.local.toRecord
import com.example.data.local.toRecordDto
import com.example.data.remote.RecordDto
import com.example.data.remote.responses.RecordsResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.upsertRecord(
    recordDataSource: RecordDataSource
) {
    authenticate {
        post("upsert_record") {
            val request = call.receiveNullable<RecordDto>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@post
            }

            val wasAcknowledged = recordDataSource.upsertRecord(
                userId = userId,
                record = request.toRecord()
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.InternalServerError)
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.records(
    recordDataSource: RecordDataSource
) {
    authenticate {
        get("records") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            val records = recordDataSource.getAllUserRecords(userId) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = RecordsResponse(records.map { it.toRecordDto() })
            )
        }
    }
}

fun Route.delete_record(
    recordDataSource: RecordDataSource
) {
    authenticate {
        delete("delete_record/{id}") {
            val recordId = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.InternalServerError)
                return@delete
            }

            val wasAcknowledged = recordDataSource.deleteRecord(userId, recordId)
            if(!wasAcknowledged){
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}