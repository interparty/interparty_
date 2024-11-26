package com.sparta.interparty.domain.review.service

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.mapper.ReviewMapper
import com.sparta.interparty.domain.review.repo.ReviewRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
) {

    @Transactional(readOnly = true)
    fun getReviews(showId: Long?): List<ReviewResDto> {
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

    @Transactional
    fun createReview(userId: Long, showId: Long, requestDto: ReviewReqDto): ReviewResDto {
        val existingReview = reviewRepository.findByUserIdAndShowId(userId, showId)
        if (existingReview != null) {
            throw CustomException(ExceptionResponseStatus.REVIEW_ALREADY_EXISTS)
        }
        val review = ReviewMapper.toEntity(userId, showId, requestDto)
        val savedReview = reviewRepository.save(review)
        return ReviewMapper.toDto(savedReview)
    }

    @Transactional
    fun updateReview(reviewId: Long, requestDto: ReviewReqDto): ReviewResDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND) }
        review.update(requestDto.comment, requestDto.rating)
        return ReviewMapper.toDto(review)
    }

    @Transactional
    fun deleteReview(reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { CustomException(ExceptionResponseStatus.REVIEW_NOT_FOUND) }
        reviewRepository.delete(review)
    }
}
