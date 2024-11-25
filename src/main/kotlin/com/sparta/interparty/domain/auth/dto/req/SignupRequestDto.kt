package com.sparta.interparty.domain.auth.dto.req

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignupRequestDto(
    val username: @NotBlank String,
    val email: @NotBlank @Email String,
    val password: @NotBlank String,
    val userRole: @NotBlank String,
    val nickname: @NotBlank String,
    val phoneNumber: @NotBlank String
)