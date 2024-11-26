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
        fun from(entity: Reservation): ReservationResDto{
            return  ReservationResDto(
                id = entity.id,
                userId = entity.reserverId.id,
                showId =  entity.showId.id,
                seat = entity.seat,
                status = entity.status
            )
        }
    }
}