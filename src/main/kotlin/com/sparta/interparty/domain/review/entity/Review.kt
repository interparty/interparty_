package com.sparta.interparty.domain.review.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val showId: UUID,

    @Column(nullable = false, length = 500)
    var comment: String,

    @Column(nullable = false)
    var rating: Int,

    @Column(nullable = false)
    var isDeleted: Boolean = false
) {
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var modifiedAt: LocalDateTime = LocalDateTime.now()

    fun updateReview(newComment: String, newRating: Int) {
        comment = newComment
        rating = newRating
        modifiedAt = LocalDateTime.now()
    }

    fun softDelete() {
        isDeleted = true
    }
}
