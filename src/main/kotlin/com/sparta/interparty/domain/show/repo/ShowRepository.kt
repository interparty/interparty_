package com.sparta.interparty.domain.show.repo

import com.sparta.interparty.domain.show.entity.Show
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ShowRepository : JpaRepository<Show, UUID> {
    fun findByIdAndIsDeletedFalse(id: UUID): Optional<Show>
}
