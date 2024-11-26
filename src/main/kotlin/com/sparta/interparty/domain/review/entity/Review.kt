package com.sparta.interparty.domain.review.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
data class Review(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val showId: Long,

    @Column(nullable = false, length = 500)
    var comment: String,

    @Column(nullable = false)
    var rating: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun update(comment: String, rating: Int) {
        this.comment = comment
        this.rating = rating
        this.updatedAt = LocalDateTime.now()
    }
}
