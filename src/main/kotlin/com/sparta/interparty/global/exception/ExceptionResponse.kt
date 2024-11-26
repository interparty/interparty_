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
            return ResponseEntity(ExceptionResponse(status, message), status)
        }
    }
}