package com.sparta.interparty.domain.reservation.dto.req

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ReservationReqDto (
    @field:NotBlank(message = "좌석 번호를 입력해주세요.")
    @field:Pattern(regexp = "^[0-9]{1,2}\$", message = "좌석 번호는 숫자로 입력해주세요.")
    val seat : Long
)