package com.sparta.interparty.domain.reservation.service

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.repo.UserRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*


@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository
) {
    @Transactional
    fun createReservation(userId: UUID, showId: UUID, request: ReservationReqDto): ReservationResDto {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ExceptionResponseStatus.USER_NOT_FOUND) }
        val show = showRepository.findById(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 요청 본문의 좌석 번호가 총 좌석 수 범위가 아닐 경우 예외 처리
        if (!(request.seat >= 1 && request.seat <= show.totalSeats)) {
            throw CustomException(ExceptionResponseStatus.RESERVE_NOT_FOUND)
        }
        // 현재 공연의 좌석에 대한 예약을 찾아 보고, 값이 있는 경우는 중복 예약이므로 예외 처리
        val existingReservation : Reservation? = reservationRepository.findByShowIdAndSeat(showId, request.seat)
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

        return ReservationResDto(
            id = savedReservation.id!!,
            userId = savedReservation.reserver.id,
            showId = savedReservation.show.id!!,
            seat = savedReservation.seat,
            status = savedReservation.status // 예약 상태
        )
    }
    fun getReservations(userId: UUID, page: Int, size: Int): Page<ReservationResDto> {
        val pageRequest : PageRequest = PageRequest.of(page, size)

        val reservations = reservationRepository.findAllByShowIdAndIsDeletedFalse(userId, pageRequest)

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
    fun softDeleteReservation(userId:UUID,Id:UUID): ReservationResDto {

        // Id 로 예약 조회 및 예외 처리
        val reservation = reservationRepository.findById(Id).orElseThrow { throw CustomException(ExceptionResponseStatus.RESERVE_NOT_FOUND) }

        /*
        이거 위에건 orElseT()
        네그렇습니다 - 재혁
        */

        // 예약 취소 권한이 없는 경우
        if (reservation.reserver.id != userId) {
            throw CustomException(ExceptionResponseStatus.INVALID_USERROLE)
        }

        reservation.softDelete()

        val deleteReservation = reservationRepository.save(reservation)

        return ReservationResDto.from(reservation)
//        return ReservationResDto(
//            id = deleteReservation.id,
//            userId = deleteReservation.reserverId.id,
//            showId = deleteReservation.showId.id!!,
//            seat = deleteReservation.seat,z
//            status = deleteReservation.status
//        )
    }
}
