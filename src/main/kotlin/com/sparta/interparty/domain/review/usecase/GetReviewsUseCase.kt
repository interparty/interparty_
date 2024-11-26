package com.sparta.interparty.domain.review.usecase

import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetReviewsUseCase(
    private val reviewRepository: ReviewRepository,
    private val reviewMapper: ReviewMapper
) {
    fun execute(showId: UUID?): List<ReviewResDto> {
        val reviews = showId?.let {
            reviewRepository.findByShowIdAndIsDeletedFalse(it)
        } ?: reviewRepository.findAllByIsDeletedFalse()

        return reviews.map { reviewMapper.toDto(it) }
    }
}
