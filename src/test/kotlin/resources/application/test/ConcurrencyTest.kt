package resources.application.test

import com.sparta.interparty.InterpartyApplication
import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.repo.ReservationRepository
import com.sparta.interparty.domain.reservation.service.ReservationService
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.domain.user.repo.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 동시성 테스트(ReservationService 의 createReservation 메서드에 대한 동시성 테스트)
 * - 동일한 좌석을 동기적으로 5번 예약
 * - 100개의 자리에 각 10명씩 총 1000개의 스레드에서 비동기적으로 예약
 *  - 예약 실패 횟수를 확인
 *  - 예약된 좌석 번호 목록을 출력하고, 중복 여부를 확인
 */

@SpringBootTest(classes = [InterpartyApplication::class])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class ConcurrencyTest(
    @Autowired private val reservationService: ReservationService,
    @Autowired private val reservationRepository: ReservationRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val showRepository: ShowRepository,
) {
    private final val testUser: User = User(
        username = "test username",
        password = "test password",
        email = "1@1",
        nickname = "test nickname",
        userRole = UserRole.USER,
        phoneNumber = "010-0000-0000"
    )

    private final val testShow: Show = Show(
        name = "test show",
        contents = "test contents",
        address = "test address",
        price = 10000,
        totalSeats = 100,
        startDateTime = LocalDateTime.MIN,
        category = ShowCategories.ETC,
        manager = testUser
    )

    @BeforeEach
    fun setUp() {
        userRepository.save(testUser)
        showRepository.save(testShow)
    }

    @AfterEach
    fun tearDown() {
        reservationRepository.deleteAll()
        showRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("같은 좌석을 동기적으로 5번 예약")
    fun withOutThread() {
        // given
        val seatNo = 1
        var collide = 0

        // when
        repeat(5) {
            try {
                val res = reservationService.createReservation(
                    testUser.id,
                    testShow.id!!,
                    ReservationReqDto(seatNo.toLong())
                )
                println("Reservation Success: ${res.seat}")
            } catch (ex: Exception) {
                collide++
                println(ex.message)
            }
        }

        // then
        assertEquals(4, collide)
    }

    @Test
    @DisplayName("100개의 자리에 각 10명씩 총 1000개의 스레드에서 비동기적으로 예약")
    fun concurrentReservationRequests() {
        // given
        userRepository.save(testUser)
        showRepository.save(testShow)
        val executorService = Executors.newFixedThreadPool(1000)
        val countDownLatch = CountDownLatch(1000)
        val collides = IntArray(1)
        collides[0] = 0

        // when
        (1..100).forEach { seatNo ->
            // 각 자리 당 10 번의 예약을 요청
            repeat(10) {
                executorService.submit {
                    // 자리 당 1번의 예매가 성공한다고 예상
                    try {
                        reservationService.createReservation(
                            testUser.id,
                            testShow.id!!,
                            ReservationReqDto(seatNo.toLong())
                        )
                    } catch (ex: Exception) {
                        // 자리 당 9번의 실패가 발생한다고 예상
                        synchronized(collides) { collides[0]++ }
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)

        // then
        val reservations = reservationRepository.findAll().map { it.seat }
        println("예매 실패 횟수: ${collides[0]} 회")
        print("예매된 좌석 번호 목록: $reservations")

        Assertions.assertAll(
            // 요청 1000번 - 자리 100개 = 900번의 실패
            { assertEquals(900, collides[0]) },
            // 중복된 좌석 번호가 없어야 함
            { assertEquals(reservations.toSet().size, reservations.size) }
        )
    }
}