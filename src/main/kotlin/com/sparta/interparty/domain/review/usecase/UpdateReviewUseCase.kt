package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Component

@Component
class UpdateReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    fun execute(reviewId: Long, dto: ReviewReqDto): ReviewResDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND) }

        if (dto.comment.isBlank() || dto.rating !in 1..10) {
            throw CustomException(ExceptionResponseStatus.INVALID_REVIEW_REQUEST)
        }

        review.update(dto.comment, dto.rating)
        return ReviewMapper.toDto(review)
    }
}
