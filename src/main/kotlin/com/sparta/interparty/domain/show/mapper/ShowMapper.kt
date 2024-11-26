package com.sparta.interparty.domain.show.mapper

import com.sparta.interparty.domain.show.dto.req.ShowPatchReqDto
import com.sparta.interparty.domain.show.dto.req.ShowPostReqDto
import com.sparta.interparty.domain.show.dto.res.ShowPostResDto
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties

object ShowMapper {
    fun toEntity(dto : ShowPostReqDto): Show {
        return Show(
            dto.name,
            dto.contents,
            dto.address,
            dto.price,
            dto.totalSeats,
            LocalDateTime.parse(dto.startDateTime),
            ShowCategories.valueOf(dto.category)
        )
    }

    fun toMap(dto : ShowPatchReqDto): Map<String, Any> {
        return ShowPatchReqDto::class.memberProperties.associateBy(
            { it.name },
            { it.get(dto)?:"" }
        ).filter { it.value != "" }
    }

    fun toDto(entity : Show): ShowPostResDto {
        return ShowPostResDto(
            entity.id,
            entity.getCreatedAt(),
            entity.getModifiedAt(),
            entity.name,
            entity.contents,
            entity.address,
            entity.price,
            entity.totalSeats,
            entity.startDateTime.toString(),
            entity.category.toString()
        )
    }
}