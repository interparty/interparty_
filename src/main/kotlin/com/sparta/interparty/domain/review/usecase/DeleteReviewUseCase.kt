package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Component

@Component
class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    fun execute(reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND) }

        reviewRepository.delete(review)
    }
}
