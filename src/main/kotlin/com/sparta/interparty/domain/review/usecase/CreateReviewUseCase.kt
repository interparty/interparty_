package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val reviewMapper: ReviewMapper
) {
    fun execute(userId: UUID, showId: UUID, dto: ReviewReqDto): ReviewResDto {
        val review = reviewMapper.toEntity(dto, userId, showId)
        val savedReview = reviewRepository.save(review)
        return reviewMapper.toDto(savedReview)
    }
}
