package com.sparta.interparty.global.exception

import org.springframework.http.HttpStatus

data class ExceptionGroupResponse(
    val status: Int,
    val messages: MutableMap<String, String>
) {
    constructor(status: HttpStatus) : this(status.value(), mutableMapOf())

    fun addMessage(fieldName: String, message: String) {
        messages[fieldName] = message
    }
}