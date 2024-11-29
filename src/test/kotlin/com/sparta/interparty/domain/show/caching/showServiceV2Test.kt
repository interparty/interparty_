package com.sparta.interparty.domain.show.caching

import com.sparta.interparty.InterpartyApplication
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.show.service.ShowServiceV2
import com.sparta.interparty.domain.user.repo.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest(classes = [InterpartyApplication::class])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class ShowServiceV2Test(
    @Autowired
    private  var userRepository: UserRepository,

    @Autowired
    private  var showRepository: ShowRepository,

    @Autowired
    private  var redisTemplate: RedisTemplate<String, Any>,

    @Autowired
    private  var showServiceV2: ShowServiceV2
) {

    @Test
    fun `100명의 유저가 랜덤 공연을 5회 조회하며 공연별 조회수 검증`() {
        // 1. 데이터 준비: 100명의 유저와 20개의 공연이 이미 DB에 삽입되어 있어야 합니다.
        val users = userRepository.findAll()
        val shows = showRepository.findAll()
        val showIds = shows.map { it.id!! }

        // 2. Redis 초기화
        redisTemplate.keys("show:*").forEach { redisTemplate.delete(it) }

        // 3. 100명의 유저가 랜덤 공연을 5회 조회
        users.forEach { user ->
            repeat(5) {
                val randomShowId = showIds.random() // 랜덤 공연 선택
                showServiceV2.readShowWithCache(randomShowId, user.id) // 공연 조회
            }
        }

        // 4. Redis에서 각 공연의 조회수를 확인
        val redisViewCounts = mutableMapOf<UUID, Long>()
        showIds.forEach { showId ->
            val viewKey = "show:$showId:views"
            val viewCount = redisTemplate.opsForValue().get(viewKey)?.toString()?.toLong() ?: 0L
            redisViewCounts[showId] = viewCount
        }

        // 5. MySQL에서 각 공연의 조회수 확인
        val dbViewCounts = showRepository.findAll().associate { it.id!! to it.viewCount.toLong() }

        // 6. 검증: Redis와 DB의 조회수 비교
        redisViewCounts.forEach { (showId, redisCount) ->
            val dbCount = dbViewCounts[showId] ?: 0L
            assertEquals(redisCount, dbCount, "Mismatch for show ID: $showId")
        }
    }
}

