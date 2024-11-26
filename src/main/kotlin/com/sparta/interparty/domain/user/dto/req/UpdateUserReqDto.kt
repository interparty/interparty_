package com.sparta.interparty.domain.user.dto.req

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateUserReqDto(
    @field:NotBlank
    val currentPassword: String,

    val newPassword: String? = null,

    @field:Email
    val email: String? = null,

    val nickname: String? = null,

    val phoneNumber: String? = null
)