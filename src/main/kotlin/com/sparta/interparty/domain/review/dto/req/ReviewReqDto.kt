package com.sparta.interparty.domain.review.dto.req

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReviewReqDto(
    @field:NotBlank(message = "댓글은 비어 있을 수 없습니다.")
    @field:Size(max = 500, message = "댓글은 최대 500자까지 입력 가능합니다.")
    val comment: String,

    @field:Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
    @field:Max(value = 10, message = "평점은 최대 10점까지 입력 가능합니다.")
    val rating: Int
)
