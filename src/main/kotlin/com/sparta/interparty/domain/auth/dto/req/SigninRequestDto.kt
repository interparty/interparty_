package com.sparta.interparty.domain.auth.dto.req

import jakarta.validation.constraints.NotBlank


data class SigninRequestDto(
    val username: @NotBlank String? = null,
    val password: @NotBlank String? = null
)