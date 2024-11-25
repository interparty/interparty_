package com.sparta.interparty.domain.user.entity

import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus

enum class UserRole {
    ADMIN, USER, MANAGER;

    companion object {
        fun of(role: String): UserRole {
            return values().find { it.name.equals(role, ignoreCase = true) }
                ?: throw CustomException(ExceptionResponseStatus.INVALID_USERROLE)
        }
    }
}