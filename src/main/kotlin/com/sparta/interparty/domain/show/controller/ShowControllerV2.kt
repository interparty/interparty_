package com.sparta.interparty.domain.show.controller

import com.sparta.interparty.domain.show.dto.res.ShowPostResDto
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.mapper.ShowMapper
import com.sparta.interparty.domain.show.service.ShowServiceV2
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v2/shows")
class ShowControllerV2(
    private val showServiceV2: ShowServiceV2
) {
    @GetMapping("/{showId}")
    fun readShowWithCache(@PathVariable showId: UUID, @AuthenticationPrincipal userDetails: UserDetailsImpl): ResponseEntity<ShowPostResDto> {
        val userId = userDetails.getUser().id
        val show: Show = showServiceV2.readShowWithCache(showId, userId)
        return ResponseEntity.ok(ShowMapper.toDto(show))
    }

    // 랭킹 동기화 작동 테스트용 api
    // 랭킹 동기화 API
    @PostMapping("/sync-rankings")
    fun syncRankings(): ResponseEntity<String> {
        showServiceV2.syncRankingsToMySQL()
        return ResponseEntity.ok("Rankings synchronized successfully.")
    }

}