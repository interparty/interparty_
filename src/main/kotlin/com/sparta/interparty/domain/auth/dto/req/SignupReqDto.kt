package com.sparta.interparty.domain.auth.dto.req

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignupReqDto(
    @field:NotBlank
    val username: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val userRole: String,

    @field:NotBlank
    val nickname: String,

    @field:NotBlank
    val phoneNumber: String
)