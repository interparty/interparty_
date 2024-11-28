package com.sparta.interparty.domain.reservation

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.service.ReservationService
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test") // 테스트 프로파일 활성화
class ReservationServiceTest {

    @Autowired
    private lateinit var reservationService: ReservationService

    @Test
    fun `동시 예약 생성 테스트`() {
        val userId = UUID.randomUUID() // 테스트 사용자 ID
        val showId = UUID.randomUUID() // 테스트 공연 ID
        val seatNumber = 1L // 테스트 좌석 번호

        // 요청 객체 생성
        val request = ReservationReqDto(seat = seatNumber)

        // 스레드 풀 생성 (100개의 요청을 동시에 보냄)
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(threadCount)

        // 성공 및 실패 횟수 기록 (Thread-Safe 방식)
        val successCount = IntArray(1) // 성공 횟수
        val failureCount = IntArray(1) // 실패 횟수

        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    reservationService.createReservation(userId, showId, request)
                    synchronized(successCount) { successCount[0]++ } // 성공 횟수 증가
                } catch (e: CustomException) {
                    if (e.exceptionResponseStatus == ExceptionResponseStatus.DUPLICATE_RESERVATION) {
                        synchronized(failureCount) { failureCount[0]++ } // 실패 횟수 증가
                    }
                }
            }
        }

        // 스레드 종료 후 결과 대기
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)

        // 테스트 결과 검증
        assertEquals(1, successCount[0], "동시에 1개의 요청만 성공해야 합니다.") // 하나의 요청만 성공해야 함
        assertEquals(threadCount - 1, failureCount[0], "나머지 요청은 실패해야 합니다.") // 나머지 요청은 실패해야 함
    }
}
