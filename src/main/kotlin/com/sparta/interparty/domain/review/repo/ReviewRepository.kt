package com.sparta.interparty.domain.review.repo

import com.sparta.interparty.domain.review.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReviewRepository : JpaRepository<Review, UUID> {
    fun findByShowIdAndIsDeletedFalse(showId: UUID): List<Review>
    fun findAllByIsDeletedFalse(): List<Review>
    fun findByIdAndIsDeletedFalse(reviewId: UUID): Optional<Review>
}
