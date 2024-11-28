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
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
    private val redisLockService: RedisLockService // Lettuce 기반 Redis Lock 의존성
) {

    @Transactional
    @Lock(key = "reservation:{#showId}:{#request.seat}", expiration = 10)
    fun createReservation(userId: UUID, showId: UUID, request: ReservationReqDto): ReservationResDto {
        // 사용자 및 공연 유효성 검사
        val user = userRepository.findById(userId).orElseThrow { CustomException(ExceptionResponseStatus.USER_NOT_FOUND) }
        val show = showRepository.findById(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 좌석 번호 유효성 검사
        if (request.seat < 1 || request.seat > show.totalSeats) {
            throw CustomException(ExceptionResponseStatus.SEAT_NOT_EXIST)
        }

        // Redis Lock Key 및 만료 시간 설정
        val lockKey = "reservation:${showId}:${request.seat}"
        val lockExpiration = 10L

        // Redis Lock 획득
        val lockId = redisLockService.acquireLock(lockKey, lockExpiration)
            ?: throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION) // Lock 실패 시 중복 예약 간주

        try {
            // 중복 예약 확인
            reservationRepository.findByShowIdAndSeat(showId, request.seat)?.let {
                throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION)
            }

            // 예약 엔티티 생성
            val reservation = Reservation(
                reserver = user,
                show = show,
                seat = request.seat
            )

            // 예약 상태 갱신
            reservation.confirmReservation()

            // 예약 데이터 저장
            val savedReservation = reservationRepository.save(reservation)

            // Commit 이후 Redis Lock 해제
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    redisLockService.releaseLock(lockKey, lockId)
                }
            })

            // 저장된 예약 데이터를 DTO로 변환하여 반환
            return ReservationResDto(
                id = savedReservation.id!!,
                userId = savedReservation.reserver.id,
                showId = savedReservation.show.id!!,
                seat = savedReservation.seat,
                status = savedReservation.status
            )
        } catch (ex: Exception) {
            redisLockService.releaseLock(lockKey, lockId)
            throw ex
        }
    }

    fun getReservations(userId: UUID, page: Int, size: Int): Page<ReservationResDto> {
        val pageable: Pageable = PageRequest.of(page, size)

        val reservations = reservationRepository.findAllByReserverIdAndIsDeletedFalse(userId, pageable)

        return reservations.map { reservation ->
            ReservationResDto(
                id = reservation.id!!,
                userId = reservation.reserver.id,
                showId = reservation.show.id!!,
                seat = reservation.seat,
                status = reservation.status
            )
        }
    }

    @Transactional
    fun softDeleteReservation(userId: UUID, reservationId: UUID) {
        val reservation = reservationRepository.findById(reservationId).orElseThrow {
            throw CustomException(ExceptionResponseStatus.RESERVE_NOT_FOUND)
        }

        if (reservation.reserver.id != userId) {
            throw CustomException(ExceptionResponseStatus.INVALID_USERROLE)
        }

        reservation.softDelete()
        reservationRepository.save(reservation)
    }
}
