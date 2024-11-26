package com.sparta.interparty.domain.review.controller

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.usecase.CreateReviewUseCase
import com.sparta.interparty.domain.review.usecase.DeleteReviewUseCase
import com.sparta.interparty.domain.review.usecase.GetReviewsUseCase
import com.sparta.interparty.domain.review.usecase.UpdateReviewUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val getReviewsUseCase: GetReviewsUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) {

    @GetMapping
    fun getReviews(@RequestParam(required = false) showId: Long?): ResponseEntity<List<ReviewResDto>> {
        val reviews = getReviewsUseCase.execute(showId)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping
    fun createReview(
        @RequestParam userId: Long,
        @RequestParam showId: Long,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val review = createReviewUseCase.execute(userId, showId, dto)
        return ResponseEntity.ok(review)
    }

    @PatchMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val updatedReview = updateReviewUseCase.execute(reviewId, dto)
        return ResponseEntity.ok(updatedReview)
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(@PathVariable reviewId: Long): ResponseEntity<Void> {
        deleteReviewUseCase.execute(reviewId)
        return ResponseEntity.noContent().build()
    }
}
