package com.sparta.interparty.domain.show.repo

import com.sparta.interparty.domain.show.entity.Show
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

// todo - 확장 Repository 추가 상속 필요

interface ShowRepository : JpaRepository<Show, UUID> {
    fun findAllByIsDeletedFalse(): List<Show>
    fun findByIdAndIsDeletedFalse(id: UUID): Optional<Show>
}