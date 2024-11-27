package com.sparta.interparty.domain.review.dto.res

import com.sparta.interparty.domain.review.entity.Review
import java.time.LocalDateTime
import java.util.UUID

data class ReviewResDto(
    val id: UUID?,
    val userId: UUID,
    val showId: UUID,
    val comment: String,
    val rating: Int,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    companion object {
        fun from(review: Review): ReviewResDto {
            return ReviewResDto(
                id = review.id!!,
                userId = review.userId,
                showId = review.showId,
                comment = review.comment,
                rating = review.rating,
                createdAt = review.createdAt,
                modifiedAt = review.modifiedAt
            )
        }
    }
}
