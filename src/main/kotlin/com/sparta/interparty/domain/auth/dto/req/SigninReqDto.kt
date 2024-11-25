package com.sparta.interparty.domain.auth.dto.req

import jakarta.validation.constraints.NotBlank


data class SigninReqDto(
    @field:NotBlank
    val username: String,

    @field:NotBlank
    val password: String
)