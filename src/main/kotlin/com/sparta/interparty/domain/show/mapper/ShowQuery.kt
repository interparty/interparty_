package com.sparta.interparty.domain.show.mapper

import com.sparta.interparty.domain.show.enums.ShowCategories
import java.time.LocalDateTime
import java.util.UUID

data class ShowQuery(
    val page: Int,
    val size: Int,
    val sortBy: String,
    val order: String,
    val category: ShowCategories?,
    val search: String?,
    val managedBy: UUID?,
    val dateAfter: LocalDateTime?,
    val dateBefore: LocalDateTime?
)