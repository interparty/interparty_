package com.sparta.interparty.domain.show.dto.req

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import jakarta.validation.constraints.Pattern
import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class ShowPostReqDto(
    @field:NotNull
    var name: String,

    @field:NotNull
    var contents: String,

    @field:NotNull
    var address: String,

    @field:NotNull
    var price: Long,

    @field:NotNull
    var totalSeats: Long,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @field:Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}",
        message = "올바른 날짜 및 시간 형식이 아닙니다. (yyyy-MM-dd'T'HH:mm)")
    var startDateTime: String,

    @field:NotNull
    var category: String
) {

    fun toEntity(): Show {
        return Show(
            name,
            contents,
            address,
            price,
            totalSeats,
            LocalDateTime.parse(startDateTime),
            ShowCategories.valueOf(category)
        )
    }
}