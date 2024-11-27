package com.sparta.interparty.domain.review.controller

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.usecase.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) {

    @GetMapping
    fun findAllReviews(@RequestParam(required = false) showId: UUID?): ResponseEntity<List<ReviewResDto>> {
        val reviews = getReviewsUseCase.execute(showId)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping
    fun addReview(
        @RequestParam userId: UUID,
        @RequestParam showId: UUID,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val review = createReviewUseCase.execute(userId, showId, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    @PatchMapping("/{reviewId}")
    fun editReview(
        @PathVariable reviewId: UUID,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val updatedReview = updateReviewUseCase.execute(reviewId, dto)
        return ResponseEntity.ok(updatedReview)
    }

    @DeleteMapping("/{reviewId}")
    fun removeReview(@PathVariable reviewId: UUID): ResponseEntity<Unit> {
        deleteReviewUseCase.execute(reviewId)
        return ResponseEntity.noContent().build()
    }
}
