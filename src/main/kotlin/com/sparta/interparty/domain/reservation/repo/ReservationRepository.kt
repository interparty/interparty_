package com.sparta.interparty.domain.reservation.repo

import com.sparta.interparty.domain.reservation.entity.Reservation
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReservationRepository : JpaRepository<Reservation, UUID> {
    fun findAllByReserverIdAndIsDeletedFalse(
        reserverId: UUID,
        pageRequest: PageRequest
    ): Page<Reservation>
    fun findByShowIdAndSeat(showId: UUID, seat: Long): Reservation?

    fun findAllByShowIdAndIsDeletedFalse(showId: UUID, pageRequest: PageRequest): Page<Reservation>
}