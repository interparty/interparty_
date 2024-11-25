package com.sparta.interparty.domain.user.repo

import com.sparta.interparty.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User
}