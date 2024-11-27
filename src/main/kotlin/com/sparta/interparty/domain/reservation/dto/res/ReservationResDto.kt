package com.sparta.interparty.domain.reservation.dto.res

import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.enums.ReservationStatus
import java.util.*

class ReservationResDto(
    val id: UUID,
    val userId : UUID,
    val showId : UUID,
    val seat : Long,
    val status : ReservationStatus
){
    companion object {
        fun from(reservation: Reservation): ReservationResDto{
            return  ReservationResDto(
                id = reservation.id!!,
                userId = reservation.reserver.id,
                showId = reservation.show.id!!,
                seat = reservation.seat,
                status = reservation.status
            )
        }
    }
}