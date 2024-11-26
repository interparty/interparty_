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
                show.id,
                show.getCreatedAt(),
                show.getModifiedAt(),
                show.name,
                show.contents,
                show.address,
                show.price,
                show.totalSeats,
                show.startDateTime.toString(),
                show.category.toString()
            )
        }
    }
}