package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Component

@Component
class GetReviewsUseCase(
    private val reviewRepository: ReviewRepository
) {
    fun execute(showId: Long?): List<ReviewResDto> {
        val reviews = if (showId != null) {
            reviewRepository.findAllByShowId(showId)
        } else {
            reviewRepository.findAll()
        }

        if (reviews.isEmpty()) {
            throw CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND)
        }

        return reviews.map { ReviewMapper.toDto(it) }
    }
}
