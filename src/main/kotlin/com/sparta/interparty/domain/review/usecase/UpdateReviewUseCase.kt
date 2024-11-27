package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val reviewMapper: ReviewMapper
) {
    @Transactional
    fun execute(reviewId: UUID, dto: ReviewReqDto): ReviewResDto {
        val review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
            .orElseThrow { IllegalArgumentException("Review not found") }
        review.updateReview(dto.comment, dto.rating)
        return reviewMapper.toDto(review)
    }
}
