package com.sparta.interparty.domain.reservation.service

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.repo.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.awt.print.Pageable
import java.util.*


@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository
) {
    @Transactional
    fun createReservation(userId: UUID, showId: UUID, request: ReservationReqDto): ReservationResDto {
        if (request.seat < 1) {
            throw IllegalArgumentException("좌석 번호는 1 이상이어야 합니다.")
        }
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
        val show = showRepository.findById(showId)
            .orElseThrow { IllegalArgumentException("Show not found") }
        val reservation = Reservation(
            id = UUID.randomUUID(),
            reserverId = user,
            showId = show,
            seat = request.seat
        )
        val savedReservation = reservationRepository.save(reservation)

        return ReservationResDto(
            id = savedReservation.id,
            userId = savedReservation.reserverId.id, // 사용자 ID
            showId = savedReservation.showId.id, // 공연 ID
            seat = savedReservation.seat,
            status = savedReservation.status // 예약 상태
        )
    }
    fun getReservations(userId: UUID, page: Int, size: Int): Page<ReservationResDto> {
        val pageable: Pageable = PageRequest.of(page, size)

        val reservations = reservationRepository.findAllByReserverIdAndIsDeletedFalse(userId, pageable)

        // Entity -> DTO 변환
        return reservations.map { reservation ->
            ReservationResDto(
                id = reservation.id,
                userId = reservation.reserverId.id,
                showId = reservation.showId.id,
                seat = reservation.seat,
                status = reservation.status
            )
        }
    }
    @Transactional
    fun softDeleteReservation(userId:UUID,Id:UUID): ReservationResDto {
        val reservation = reservationRepository.findById(Id)
            .orElseThrow{IllegalArgumentException("예약을 찾을 수 없습니다.")
            }
        if (reservation.reserverId.id!= userId) {
            throw IllegalAccessException("이 예약을 취소할 권한이 없습니다.")
        }

        reservation.softDelete()

        val deleteReservation = reservationRepository.save(reservation)

        return ReservationResDto(
            id = deleteReservation.id,
            userId = deleteReservation.reserverId.id,
            showId = deleteReservation.showId.id,
            seat = deleteReservation.seat,
            status = deleteReservation.status
        )
    }
}
