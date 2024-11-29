package com.sparta.interparty.domain.user.repo

import com.sparta.interparty.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User
}