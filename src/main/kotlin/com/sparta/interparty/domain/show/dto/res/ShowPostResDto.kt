package com.sparta.interparty.domain.show.dto.res

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
    val category: String = "",
    // Redis 캐시 추가 사항
    val viewCount: Int = 0,
    val rankings: MutableMap<Int, UUID>? = null
)