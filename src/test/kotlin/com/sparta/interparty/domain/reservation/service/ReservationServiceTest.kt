package com.sparta.interparty.domain.reservation.service

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.entity.Reservation
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.domain.user.repo.UserRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import com.sparta.interparty.global.redis.RedisLockService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
class ReservationServiceTest {

    private lateinit var reservationService: ReservationService
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var userRepository: UserRepository
    private lateinit var showRepository: ShowRepository
    private lateinit var redisLockService: RedisLockService

    @BeforeEach
    fun setUp() {
        reservationRepository = mock(ReservationRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        showRepository = mock(ShowRepository::class.java)
        redisLockService = mock(RedisLockService::class.java)

        reservationService = ReservationService(
            reservationRepository,
            userRepository,
            showRepository,
            redisLockService
        )
    }

    @Test
    fun `예약 생성 - 성공`() {
        // Arrange
        val userId = UUID.randomUUID()
        val showId = UUID.randomUUID()
        val request = ReservationReqDto(seat = 5)

        val mockUser = User(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            nickname = "testNickname",
            userRole = UserRole.USER,
            phoneNumber = "01012345678"
        )

        val mockShow = Show(
            id = showId,
            name = "testShow",
            totalSeats = 10
        )

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(showRepository.findById(showId)).thenReturn(Optional.of(mockShow))
        `when`(reservationRepository.findByShowIdAndSeat(showId, request.seat)).thenReturn(null)
        `when`(redisLockService.acquireLock("reservation:$showId:${request.seat}", 10L)).thenReturn("mock-lock-id")

        val mockReservation = Reservation(
            reserver = mockUser,
            show = mockShow,
            seat = request.seat
        ).apply { id = UUID.randomUUID() }

        `when`(reservationRepository.save(any(Reservation::class.java))).thenReturn(mockReservation)

        // Act
        val result = reservationService.createReservation(userId, showId, request)

        // Assert
        assertNotNull(result)
        assertEquals(mockReservation.id, result.id)
        assertEquals(request.seat, result.seat)
        verify(redisLockService).releaseLock("reservation:$showId:${request.seat}", "mock-lock-id")
    }

    @Test
    fun `예약 생성 - 중복 좌석 예외`() {
        // Arrange
        val userId = UUID.randomUUID()
        val showId = UUID.randomUUID()
        val request = ReservationReqDto(seat = 5)

        val mockUser = User(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            nickname = "testNickname",
            userRole = UserRole.USER,
            phoneNumber = "01012345678"
        )

        val mockShow = Show(
            id = showId,
            name = "testShow",
            totalSeats = 10
        )

        val mockReservation = Reservation(
            reserver = mockUser,
            show = mockShow,
            seat = request.seat
        ).apply { id = UUID.randomUUID() }

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(showRepository.findById(showId)).thenReturn(Optional.of(mockShow))
        `when`(reservationRepository.findByShowIdAndSeat(showId, request.seat)).thenReturn(mockReservation)

        // Act & Assert
        val exception = assertThrows(CustomException::class.java) {
            reservationService.createReservation(userId, showId, request)
        }
        assertEquals(ExceptionResponseStatus.DUPLICATE_RESERVATION, exception.exceptionResponseStatus)
    }

    @Test
    fun `예약 생성 - 사용자 없음 예외`() {
        // Arrange
        val userId = UUID.randomUUID()
        val showId = UUID.randomUUID()
        val request = ReservationReqDto(seat = 5)

        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(CustomException::class.java) {
            reservationService.createReservation(userId, showId, request)
        }
        assertEquals(ExceptionResponseStatus.USER_NOT_FOUND, exception.exceptionResponseStatus)
    }

    @Test
    fun `예약 생성 - 공연 없음 예외`() {
        // Arrange
        val userId = UUID.randomUUID()
        val showId = UUID.randomUUID()
        val request = ReservationReqDto(seat = 5)
        val mockUser = User(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            nickname = "testNickname",
            userRole = UserRole.USER,
            phoneNumber = "01012345678"
        )

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(showRepository.findById(showId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(CustomException::class.java) {
            reservationService.createReservation(userId, showId, request)
        }
        assertEquals(ExceptionResponseStatus.SHOW_NOT_FOUND, exception.exceptionResponseStatus)
    }

    @Test
    fun `예약 생성 - 좌석 범위 초과 예외`() {
        // Arrange
        val userId = UUID.randomUUID()
        val showId = UUID.randomUUID()
        val request = ReservationReqDto(seat = 15) // 초과된 좌석 번호
        val mockUser = User(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            nickname = "testNickname",
            userRole = UserRole.USER,
            phoneNumber = "01012345678"
        )

        val mockShow = Show(
            id = showId,
            name = "testShow",
            totalSeats = 10
        )

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(showRepository.findById(showId)).thenReturn(Optional.of(mockShow))

        // Act & Assert
        val exception = assertThrows(CustomException::class.java) {
            reservationService.createReservation(userId, showId, request)
        }
        assertEquals(ExceptionResponseStatus.SEAT_NOT_EXIST, exception.exceptionResponseStatus)
    }
}
