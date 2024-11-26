package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository
) {

    @Transactional
    fun execute(reviewId: UUID) {
        val review = reviewRepository.findById(reviewId).orElseThrow {
            CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND)
        }

        review.softDelete()
        reviewRepository.save(review)
    }
}
