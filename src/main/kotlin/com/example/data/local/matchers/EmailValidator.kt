package com.example.data.local.matchers

fun interface EmailValidator {
    fun validate(email: String): Boolean
}