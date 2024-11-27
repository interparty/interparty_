package com.sparta.interparty.domain.user.entity

import com.sparta.interparty.global.entity.TimeStamped
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var userRole: UserRole,

    @Column(nullable = false)
    var phoneNumber: String,
) : TimeStamped() {
    @Id
    val id: UUID = UUID.randomUUID()

    @Column(nullable = false)
    var isDeleted: Boolean = false
}