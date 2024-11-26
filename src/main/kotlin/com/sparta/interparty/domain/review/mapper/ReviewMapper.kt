package com.sparta.interparty.domain.review.mapper

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.entity.Review

object ReviewMapper {

    fun toEntity(userId: Long, showId: Long, dto: ReviewReqDto): Review {
        return Review(
            userId = userId,
            showId = showId,
            comment = dto.comment,
            rating = dto.rating
        )
    }

    fun toDto(entity: Review): ReviewResDto {
        return ReviewResDto(
            id = entity.id,
            userId = entity.userId,
            showId = entity.showId,
            comment = entity.comment,
            rating = entity.rating,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
