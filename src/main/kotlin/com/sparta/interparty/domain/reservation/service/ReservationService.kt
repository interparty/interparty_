package com.sparta.interparty.domain.reservation.service

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.repo.UserRepository
import com.sparta.interparty.global.aop.Lock
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import com.sparta.interparty.global.redis.RedisLockService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
    private val redisLockService: RedisLockService
) {

    @Transactional
    @Lock(key = "reservation:{#showId}:{#request.seat}", expiration = 10)
    fun createReservation(userId: UUID, showId: UUID, request: ReservationReqDto): ReservationResDto {
        val user = userRepository.findById(userId).orElseThrow {
            CustomException(ExceptionResponseStatus.USER_NOT_FOUND)
        }

        val show = showRepository.findById(showId).orElseThrow {
            CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND)
        }

        if (request.seat < 1 || request.seat > show.totalSeats) {
            throw CustomException(ExceptionResponseStatus.SEAT_NOT_EXIST)
        }

        val lockKey = "reservation:$showId:${request.seat}"
        val lockExpiration = 10L
        val lockId = redisLockService.acquireLock(lockKey, lockExpiration)
            ?: throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION)

        try {
            reservationRepository.findByShowIdAndSeat(showId, request.seat)?.let {
                throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION)
            }

            val reservation = Reservation(
                reserver = user,
                show = show,
                seat = request.seat
            ).apply { confirmReservation() }

            val savedReservation = reservationRepository.save(reservation)

            return mapToReservationResDto(savedReservation)
        } finally {
            redisLockService.releaseLock(lockKey, lockId)
        }
    }

    fun getReservations(userId: UUID, page: Int, size: Int): Page<ReservationResDto> {
        val pageable: Pageable = PageRequest.of(page, size)

        val reservations = reservationRepository.findAllByReserverIdAndIsDeletedFalse(userId, pageable)

        return reservations.map { reservation ->
            mapToReservationResDto(reservation)
        }
    }

    @Transactional
    fun softDeleteReservation(userId: UUID, reservationId: UUID) {
        val reservation = reservationRepository.findById(reservationId).orElseThrow {
            CustomException(ExceptionResponseStatus.RESERVE_NOT_FOUND)
        }

        if (reservation.reserver.id != userId) {
            throw CustomException(ExceptionResponseStatus.INVALID_USERROLE)
        }

        reservation.softDelete()
        reservationRepository.save(reservation)
    }

    private fun mapToReservationResDto(reservation: Reservation): ReservationResDto {
        return ReservationResDto(
            id = reservation.id!!,
            userId = reservation.reserver.id,
            showId = reservation.show.id!!,
            seat = reservation.seat,
            status = reservation.status
        )
    }
}
