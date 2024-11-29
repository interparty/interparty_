package resources.application.test

import com.sparta.interparty.InterpartyApplication
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.domain.user.repo.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest(classes = [InterpartyApplication::class])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class ShowV2Test(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val showRepository: ShowRepository
) {
    private val numberOfUsers = 100
    private val numberOfShows = 20

    @BeforeEach
    fun setup(): Unit = runBlocking {
        // 1. 유저 100명 삽입
        val users = (1..numberOfUsers).map {
            User(
                username = "user$it",
                password = "password$it",
                email = "user$it@example.com",
                nickname = "User$it",
                userRole = UserRole.USER,
                phoneNumber = "010-0000-00$it"
            )
        }
        userRepository.saveAll(users)

        // 2. 공연 20개 삽입
        val shows = (1..numberOfShows).map {
            Show(
                name = "Show $it",
                contents = "Contents for Show $it",
                address = "Address $it",
                price = 1000L,
                totalSeats = 100L,
                startDateTime = LocalDateTime.now(),
                category = ShowCategories.ETC
            )
        }
        showRepository.saveAll(shows)
    }

    @Test
    fun `100명의 유저가 랜덤 공연 5회 조회`() = runBlocking {
        // 1. 유저와 공연 데이터 로드
        val users = userRepository.findAll()
        val shows = showRepository.findAll()

        // 2. 조회수 기록용 맵
        val showViewCounts = mutableMapOf<UUID, Int>()

        shows.forEach { show -> showViewCounts[show.id!!] = 0 }

        // 3. 유저 100명이 랜덤 공연 5회 조회
        users.forEach { user ->
            repeat(5) {
                val randomShow = shows.random()
                incrementViewCount(randomShow.id!!, user.id!!)
                showViewCounts[randomShow.id!!] = showViewCounts[randomShow.id!!]!! + 1
            }
        }

        // 4. 결과 검증
        showViewCounts.forEach { (showId, count) ->
            val actualViewCount = showRepository.findById(showId).get().viewCount
            println("Show $showId Expected: $count, Actual: $actualViewCount")
            assertEquals(count, actualViewCount)
        }
    }

    private fun incrementViewCount(showId: UUID, userId: UUID) = runBlocking {
        // Redis 또는 데이터베이스의 조회수 증가 로직을 호출
        val show = showRepository.findById(showId).orElseThrow()
        show.viewCount++
        showRepository.save(show)
    }
}
