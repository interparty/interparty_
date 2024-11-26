package com.sparta.interparty.domain.show.dto.res

import com.sparta.interparty.domain.show.entity.Show
import java.time.LocalDateTime
import java.util.UUID

data class ShowPostResDto(
    val id: UUID? = null,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val name: String = "",
    val contents: String = "",
    val address: String = "",
    val price: Long = 0,
    val totalSeats: Long = 0,
    val startDateTime: String = "",
    val category: String = ""
) {
    companion object {
        fun of(show: Show): ShowPostResDto {
            return ShowPostResDto(
                show.getId(),
                show.getCreatedAt(),
                show.getModifiedAt(),
                show.getName(),
                show.getContents(),
                show.getAddress(),
                show.getPrice(),
                show.getTotalSeats(),
                show.getStartDateTime().toString(),
                show.getCategory().toString()
            )
        }
    }
}