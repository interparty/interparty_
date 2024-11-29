package com.sparta.interparty.domain.reservation.service

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.repo.UserRepository
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
    private val redisLockService: RedisLockService // Redis Lock 의존성 추가
) {
    @Transactional
    fun createReservation(userId: UUID, showId: UUID, request: ReservationReqDto): ReservationResDto {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ExceptionResponseStatus.USER_NOT_FOUND) }
        val show = showRepository.findById(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 요청 본문의 좌석 번호가 총 좌석 수 범위가 아닐 경우 예외 처리
        if (!(request.seat >= 1 && request.seat <= show.totalSeats)) {
            throw CustomException(ExceptionResponseStatus.SEAT_NOT_EXIST)
        }

        // Redis Lock Key 와 만료 시간 설정
        val lockKey = "reservation:${showId}:${request.seat}" // 공연 ID와 좌석 번호를 포함한 고유 Key
        val lockExpiration = 10L // Lock 만료 시간 (10초)

        val lockId = redisLockService.acquireLock(lockKey, lockExpiration) // Redis Lock 획득
        lockId ?: throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION) // Lock 획득 실패 시 중복 예약으로 간주

        // Lock 획득 성공 시점부터 정합성에 영향이 가는 작업 시작
        try {
            // 현재 공연의 좌석에 대한 예약을 찾아 보고, 값이 있는 경우는 중복 예약이므로 예외 처리
            val existingReservation: Reservation? = reservationRepository.findByShowIdAndSeat(showId, request.seat)
            existingReservation?.let { throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION) }

            // 등록할 예약 엔티티 생성
            val reservation = Reservation(
                reserver = user,
                show = show,
                seat = request.seat
            )

            // 예약 엔티티 상태 갱신
            reservation.confirmReservation()

            // 저장 및 반환
            val savedReservation = reservationRepository.save(reservation)

            // Transactional 에 의한 변경사항 Commit 이 완료된 후에 여하 로직이 동작하도록 미리 설정
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    // Lock 은 Commit 이 완료된 후에 해제되여야 빈틈이 없음
                    redisLockService.releaseLock(lockKey, lockId)
                }
            })

            return ReservationResDto(
                id = savedReservation.id!!,
                userId = savedReservation.reserver.id,
                showId = savedReservation.show.id!!,
                seat = savedReservation.seat,
                status = savedReservation.status
            )
        } catch(ex: Exception) {
            // 정상적으로 Transaction 이 Commit 될 경우 Lock 이 해제되기 때문에, 예외가 발생했을 때만 추가적으로 Lock 해제
            redisLockService.releaseLock(lockKey, lockId)
            throw ex
        }
    }

        fun getReservations(userId: UUID, page: Int, size: Int): Page<ReservationResDto> {
            val pageable: Pageable = PageRequest.of(page, size)

            val reservations = reservationRepository.findAllByReserverIdAndIsDeletedFalse(userId, pageable)

            // Entity -> DTO 변환
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

            // Id 로 예약 조회 및 없을 경우 예외 처리
            val reservation = reservationRepository.findById(reservationId).orElseThrow {
                throw CustomException(ExceptionResponseStatus.RESERVE_NOT_FOUND)
            }

            // 예약 사용자와 삭제 유저 사용자가 다를 경우 예외 처리
            if (reservation.reserver.id != userId) {
                throw CustomException(ExceptionResponseStatus.INVALID_USERROLE)
            }

            // 삭제 상태 업데이트 및 저장
            reservation.softDelete()
            reservationRepository.save(reservation)
        }
    }