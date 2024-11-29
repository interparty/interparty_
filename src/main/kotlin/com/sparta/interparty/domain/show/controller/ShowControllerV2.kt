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

    /* 테스트용 api */
    // 랭킹 동기화 API
    @PostMapping("/sync-rankings")
    fun syncRankings(): ResponseEntity<String> {
        showServiceV2.syncRankingsToMySQL()
        return ResponseEntity.ok("랭킹 동기화가 완료되었습니다")
    }

    // 랭킹 목록 조회 API
    @GetMapping("/rankings")
    fun getRankings(): ResponseEntity<List<Map<String, Any>>> {
        val rankings = showServiceV2.getRankings()
        return ResponseEntity.ok(rankings)
    }

//    조회수 동기화 API.. 현재 오류 발생
//    @PostMapping("/sync-rankings")
//    fun syncViewCounts(): ResponseEntity<String> {
//        showServiceV2.syncViewCountsToMySQL()
//        return ResponseEntity.ok("조회수 동기화가 완료되었습니다")
//    } 어차피 한시간에 한번씩 동기화 되므로 시연 영상에선 제외

}