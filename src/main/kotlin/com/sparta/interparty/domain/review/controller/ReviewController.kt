package com.sparta.interparty.domain.review.controller

import com.sparta.interparty.domain.review.dto.req.ReviewReqDto
import com.sparta.interparty.domain.review.dto.res.ReviewResDto
import com.sparta.interparty.domain.review.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @GetMapping
    fun getReviews(@RequestParam(required = false) showId: Long?): ResponseEntity<List<ReviewResDto>> {
        val reviews = reviewService.getReviews(showId)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping
    fun createReview(
        @RequestParam userId: Long,
        @RequestParam showId: Long,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val review = reviewService.createReview(userId, showId, dto)
        return ResponseEntity.ok(review)
    }

    @PatchMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody dto: ReviewReqDto
    ): ResponseEntity<ReviewResDto> {
        val updatedReview = reviewService.updateReview(reviewId, dto)
        return ResponseEntity.ok(updatedReview)
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(@PathVariable reviewId: Long): ResponseEntity<Void> {
        reviewService.deleteReview(reviewId)
        return ResponseEntity.noContent().build()
    }
}
