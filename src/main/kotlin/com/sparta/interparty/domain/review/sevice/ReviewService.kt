package com.sparta.interparty.domain.review.service

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewMapper: ReviewMapper
) {

    @Transactional(readOnly = true)
    fun getReviews(showId: UUID?): List<ReviewResDto> {
        val reviews = showId?.let {
            reviewRepository.findByShowIdAndIsDeletedFalse(it)
        } ?: reviewRepository.findAllByIsDeletedFalse()

        return reviews.map { reviewMapper.toDto(it) }
    }

    @Transactional
    fun createReview(userId: UUID, showId: UUID, dto: ReviewReqDto): ReviewResDto {
        val review = reviewMapper.toEntity(dto, userId, showId)
        reviewRepository.save(review)
        return reviewMapper.toDto(review)
    }

    @Transactional
    fun updateReview(reviewId: UUID, dto: ReviewReqDto): ReviewResDto {
        val review = reviewRepository.findByIdAndIsDeletedFalse(reviewId).orElseThrow {
            CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND)
        }
        review.updateReview(dto.comment, dto.rating)
        return reviewMapper.toDto(review)
    }

    @Transactional
    fun deleteReview(reviewId: UUID) {
        val review = reviewRepository.findByIdAndIsDeletedFalse(reviewId).orElseThrow {
            CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND)
        }
        review.softDelete()
    }
}
