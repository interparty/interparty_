package com.sparta.interparty.domain.review.dto.res

import java.time.LocalDateTime

data class ReviewResDto(
    val id: Long,
    val userId: Long,
    val showId: Long,
    val comment: String,
    val rating: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
