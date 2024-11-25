package com.sparta.interparty.domain.user.entity

import com.sun.jdi.request.InvalidRequestStateException

enum class UserRole {
    ADMIN, USER, MANAGER;

    companion object {
        fun of(role: String): UserRole {
            return values().find { it.name.equals(role, ignoreCase = true) }
                ?: throw InvalidRequestStateException("유효하지 않은 UserRole")
        }
    }
}