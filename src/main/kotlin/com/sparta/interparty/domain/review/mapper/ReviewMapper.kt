package com.sparta.interparty.domain.review.mapper

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.entity.Review
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReviewMapper {
    fun toEntity(dto: ReviewReqDto, userId: UUID, showId: UUID): Review {
        return Review(
            userId = userId,
            showId = showId,
            comment = dto.comment,
            rating = dto.rating
        )
    }

    fun toDto(review: Review): ReviewResDto {
        return ReviewResDto(
            id = review.id,
            userId = review.userId,
            showId = review.showId,
            comment = review.comment,
            rating = review.rating,
            createdAt = review.createdAt,
            modifiedAt = review.modifiedAt
        )
    }
}
