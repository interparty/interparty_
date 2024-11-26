package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Component

@Component
class CreateReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    fun execute(userId: Long, showId: Long, dto: ReviewReqDto): ReviewResDto {
        val existingReview = reviewRepository.findByUserIdAndShowId(userId, showId)
        if (existingReview != null) {
            throw CustomException(ExceptionResponseStatus.REVIEW_ALREADY_EXISTS)
        }

        val review = ReviewMapper.toEntity(userId, showId, dto)
        val savedReview = reviewRepository.save(review)
        return ReviewMapper.toDto(savedReview)
    }
}
