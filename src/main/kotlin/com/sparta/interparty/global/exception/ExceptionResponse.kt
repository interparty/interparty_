package com.sparta.interparty.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ExceptionResponse(
    val status: Int,
    val message: String
) {
    constructor(exceptionResponseStatus: ExceptionResponseStatus) : this(exceptionResponseStatus.status.value(), exceptionResponseStatus.message)
    constructor(status: HttpStatus, message: String) : this(status.value(), message)

    companion object {
        fun toResponseEntityWith(status: HttpStatus, message: String): ResponseEntity<ExceptionResponse> {
            requireNotNull(status) { "HttpStatus cannot be null." }
            require(!message.isNullOrBlank()) { "Message cannot be null or blank." }
            return ResponseEntity(ExceptionResponse(status, message), status)
        }
    }
}