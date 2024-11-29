package com.sparta.interparty.domain.show.repo

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

// todo - 확장 Repository 추가 상속 필요
@Repository
interface ShowRepository : JpaRepository<Show, UUID> {
    fun findAllByIsDeletedFalse(): List<Show>
    fun findByIdAndIsDeletedFalse(id: UUID): Optional<Show>

    // redis 캐시 추가 사항
    @Modifying
    @Transactional
    @Query("UPDATE Show s SET s.viewCount = :viewCount WHERE s.id = :showId")
    fun updateViewCount(showId: UUID, viewCount: Long)

//    @Transactional
//    fun updateRankings(showId: UUID, rankings: MutableMap<Int, UUID>) {
//        val show = findById(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }
//        show.rankings = rankings
//        save(show)  >> 레포지토리를 통해 랭킹을 저장할 필요가 없음. 서비스에서 처리
//    }
}