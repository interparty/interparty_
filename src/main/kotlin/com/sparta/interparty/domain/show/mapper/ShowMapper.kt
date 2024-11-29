package com.sparta.interparty.domain.show.mapper

import com.sparta.interparty.domain.show.dto.req.ShowPatchReqDto
import com.sparta.interparty.domain.show.dto.req.ShowPostReqDto
import com.sparta.interparty.domain.show.dto.res.ShowPostResDto
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties

object ShowMapper {
    fun toEntity(dto: ShowPostReqDto): Show {
        return Show(
            name = dto.name,
            contents = dto.contents,
            address = dto.address,
            price = dto.price, // Long 그대로 사용
            totalSeats = dto.totalSeats, // Long 그대로 사용
            startDateTime = LocalDateTime.parse(dto.startDateTime), // String -> LocalDateTime 변환
            category = ShowCategories.valueOf(dto.category) // String -> Enum 변환
        )
    }

    fun toMap(dto: ShowPatchReqDto): Map<String, Any> {
        return ShowPatchReqDto::class.memberProperties.associateBy(
            { it.name },
            { it.get(dto) ?: "" }
        ).filter { it.value != "" }
    }

    fun toDto(entity: Show): ShowPostResDto {
        return ShowPostResDto(
            id = entity.id, // UUID 그대로 사용
            createdAt = entity.getCreatedAt() ?: LocalDateTime.now(), // LocalDateTime 그대로 사용
            modifiedAt = entity.getModifiedAt() ?: LocalDateTime.now(), // LocalDateTime 그대로 사용
            name = entity.name,
            contents = entity.contents,
            address = entity.address,
            price = entity.price, // Long 그대로 사용
            totalSeats = entity.totalSeats, // Long 그대로 사용
            startDateTime = entity.startDateTime?.toString() ?: "", // LocalDateTime -> String 변환
            category = entity.category.toString() // Enum -> String 변환
        )
    }
}
