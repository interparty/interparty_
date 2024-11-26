package com.sparta.interparty.domain.user.dto.res

import java.util.UUID

data class UserResDto(
    val id: UUID,
    val username: String,
    val email: String,
    val nickname: String,
    val phoneNumber: String,
    val userRole: String
)