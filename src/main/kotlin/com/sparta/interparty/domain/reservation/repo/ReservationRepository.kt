package com.sparta.interparty.domain.reservation.repo

import com.sparta.interparty.domain.reservation.entity.Reservation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReservationRepository : JpaRepository<Reservation, UUID> {

    fun findAllByReserverIdAndIsDeletedFalse(
        reserverId: UUID,
        pageable: Pageable
    ): Page<Reservation>

    fun findByShowIdAndSeat(showId: UUID, seat: Long): Reservation?

}