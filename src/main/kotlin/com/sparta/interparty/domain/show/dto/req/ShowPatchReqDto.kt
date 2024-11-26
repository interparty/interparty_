package com.sparta.interparty.domain.show.dto.req

import jakarta.validation.constraints.Pattern
import org.springframework.format.annotation.DateTimeFormat
import kotlin.reflect.full.memberProperties

data class ShowPatchReqDto(
    var name: String? = null,
    var contents: String? = null,
    var address: String? = null,
    var price: Long? = null,
    var totalSeats: Long? = null,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @field:Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}",
        message = "올바른 날짜 및 시간 형식이 아닙니다. (yyyy-MM-dd'T'HH:mm)")
    var startDateTime: String? = null,

    var category: String? = null
)

inline fun <reified T : Any> T.toMap(): Map<String, Any> {
    return T::class.memberProperties.associateBy(
        { it.name },
        { it.get(this)?:"" }
    ).filter { it.value != "" }
}