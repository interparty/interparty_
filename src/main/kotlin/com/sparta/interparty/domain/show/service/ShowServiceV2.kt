package com.sparta.interparty.domain.show.service

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

/*
Redis 캐싱 로직과 캐싱을 적용한 V2 서비스 로직을 처리하는 ShowServiceV2
 */
@Service
class ShowServiceV2(
    val showRepository: ShowRepository,
    private val redisTemplate: RedisTemplate<String, Any> // RedisTemplate 주입
) {

    /*
    어뷰징 방지
    공연 조회v2 api에 적용
    */
    fun isAbuse(showId: UUID, userId: UUID): Boolean {
        // Redis 키: 특정 공연의 사용자별 조회수를 관리
        val userViewKey = "show:$showId:user-vies"

        // 사용자 조회수 증가
        val userViewCount = redisTemplate.opsForHash<String, Int>().increment(userViewKey, userId.toString(), 1) ?: 0

        // 어뷰징 감지: 조회수가 10 이상인 경우
        val isAbusing = userViewCount >= 10

        // 어뷰징 감지된 경우 TTL 설정 (1시간)
        if (isAbusing) {
            redisTemplate.expire(userViewKey, Duration.ofHours(1))
        }

        return isAbusing
    }

    // Redis 캐싱이 적용된 공연 조회
    fun readShowWithCache(showId: UUID, userId: UUID): Show {

//        대량 조회 테스트를 위해 어뷰징 감지는 주석 처리
//        어뷰징 방지 로직: 동일한 사용자가 10회 이상 조회시 감지
//        if (isAbuse(showId, userId)) {
//            throw CustomException(ExceptionResponseStatus.ABUSE_DETECTED)
//        }

        // Redis 조회수 증가
        incrementViewCount(showId)

        // Redis에서 현재 조회수 읽기
        val viewKey = "show:$showId:views"
        val viewCount = redisTemplate.opsForValue().get(viewKey)?.toString()?.toInt() ?: 0
        println("View count fetched for $viewKey: $viewCount")

        // MySQL에서 공연 정보 조회
        val show = showRepository.findByIdAndIsDeletedFalse(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 공연 객체에 조회수 설정
        show.viewCount = viewCount
        return show

    }

    @Transactional
    fun updateRankings(showId: UUID, rankings: MutableMap<Int, UUID>) {
        val show =
            showRepository.findById(showId).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        show.rankings = rankings
        showRepository.save(show)
    }

    /*
    rankingKey는 Redis의 Sorted Set 키로 사용
    모든 공연의 랭킹 데이터를 이 키에 저장
    Redis Sorted Set은 각 데이터가 점수(Score)를 가지며, 이 점수를 기준으로 정렬
     */
    private val rankingKey = "show:ranking"

    /**
     * Redis에서 조회수를 증가시키고 랭킹 정보를 업데이트합니다.
     * @param showId 공연 UUID
     */
    private fun incrementViewCount(showId: UUID) {

        // 각 공연의 조회수를 저장하는 Redis 키
        val viewKey = "show:$showId:views"

        /*
        Redis 조회수 증가
        increment() : Redis의 String 타입 데이터를 증가시키는 메서드
        viewKey에 저장된 값(조회수)을 1 증가시키고, 증가된 값을 반환. 처음 값은 1로 자동 초기화
         */
        val updatedViewCount = redisTemplate.opsForValue().increment(viewKey) ?: 0

        /*
        Sorted Set에 공연 랭킹 업데이트
        opsForZSet() : Redis의 Sorted Set 데이터를 다루는 메서드를 반환
        add() : rankingKey(Sorted Set)에 공연 ID(showId)를 추가하거나, 이미 존재하는 경우 점수를 업데이트
        점수(Score)로 updatedViewCount(증가된 조회수)를 사용
        -> Redis의 Sorted Set은 점수(Score)를 기준으로 데이터가 자동 정렬
         */
        redisTemplate.opsForZSet().add(rankingKey, showId.toString(), updatedViewCount.toDouble())
    }

    /**
     * Redis 데이터(조회수)를 1시간마다 MySQL로 동기화합니다.
     */
    fun syncViewCountsToMySQL() {
        // show:*:views 패턴을 사용하여, 특정 공연의 조회수 키를 모두 검색. ex) show:123e4567-e89b-12d3-a456-426614174000:views
        val keys = redisTemplate.keys("show:*:views")
        // Redis에서 가져온 각 키를 순회하며 조회수 데이터를 처리
        keys?.forEach { key ->
            try {
                val showId = UUID.fromString(key.split(":")[1])
                val viewCount = redisTemplate.opsForValue().get(key)?.toString()?.toInt() ?: 0

                // MySQL 업데이트
                val show = showRepository.findById(showId).orElseThrow()
                show.viewCount = viewCount
                showRepository.save(show)
            } catch (e: Exception) {
                println("Error syncing view count for key $key: ${e.message}")
            }
        }
    }

    /*
     * 자정마다 Redis의 랭킹 데이터를 MySQL로 동기화 후 초기화
     */
    fun syncRankingsToMySQL() {
        val rankings = redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, 4)

        if (rankings != null) {
            val rankingMap = mutableMapOf<Int, UUID>()

            rankings.forEachIndexed { index, entry ->
                val rank = index + 1
                val showId = UUID.fromString(entry.value.toString())
                rankingMap[rank] = showId
            }

            // 기존 랭킹 업데이트
            rankingMap.forEach { (rank, showId) ->
                val show = showRepository.findById(showId).orElseThrow {
                    throw CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND)
                }
                show.rankings[rank] = showId
                showRepository.save(show)
            }

            // Redis 데이터는 유지
            println("랭킹 동기화 완료. Redis 데이터를 유지합니다.")
        } else {
            println("Redis에 랭킹 데이터가 없습니다.")
        }

        // 조회수만 초기화
        resetViewCounts()
    }

    private fun resetViewCounts() {
        val viewKeys = redisTemplate.keys("show:*:views") ?: return

        viewKeys.forEach { key ->
            redisTemplate.delete(key)

            //삭제 확인
            if(redisTemplate.hasKey(key) == true) {
                println("Failed to delete key: $key")
            } else {
                println("Successfully deleted key: $key")
            }
        }
    }

    fun getRankings(): List<Map<String, Any>> {
        // Redis Sorted Set에서 랭킹 데이터 조회
        val rankings = redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, 4)

        // 랭킹 데이터를 반환 형식으로 변환
        return rankings?.mapIndexed { index, entry ->
            val rank = index + 1
            val showId = UUID.fromString(entry.value.toString())
            val viewCount = entry.score?.toInt() ?: 0

            // Map 형태로 변환
            mapOf(
                "rank" to rank,
                "showId" to showId,
                "viewCount" to viewCount
            )
        } ?: emptyList() // Redis에 랭킹 데이터가 없으면 빈 리스트 반환
    }

}

// 두 번 째 요구사항 : 응답 속도를 높이는 방안 -> HashSet으로 캐시 저장
//    fun incrementViewCountUsingHash(showId: UUID) {
//        val hashKey = "show:views"
//        redisTemplate.opsForHash<String, Int>().increment(hashKey,showId.toString(), 1)
//    }
// 이걸로도 따로 테스트해서 성능 비교 하고 싶었으나 시간 부족.