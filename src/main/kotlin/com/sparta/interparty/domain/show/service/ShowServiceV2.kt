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
    fun preventAbuse(showId: UUID, userId: UUID): Boolean {
        val userSetKey = "show:$showId:users"

        // Redis에서 중복 확인
        val alreadyInteracted = redisTemplate.opsForSet().isMember(userSetKey, userId) ?: false

        if (!alreadyInteracted) {
            // 사용자 기록 및 TTL 설정
            redisTemplate.opsForSet().add(userSetKey, userId)
            redisTemplate.expire(userSetKey, Duration.ofHours(1))
        }

        return alreadyInteracted
    }

    // Redis 캐싱이 적용된 공연 조회
    fun readShowWithCache(showId: UUID, userId: UUID): Show {

        // 어뷰징 방지 로직: 동일 사용자가 중복 요청을 보냈는지 확인
        val isAbusing = preventAbuse(showId, userId)
        if (isAbusing) {
            throw CustomException(ExceptionResponseStatus.ABUSE_DETECTED)
        }

        // Redis 조회수 증가
        incrementViewCount(showId)

        // MySQL에서 공연 정보 조회
        return showRepository.findByIdAndIsDeletedFalse(showId)
            .orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }
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

//            // MySQL에 저장
//            val show = showRepository.findById(rankingMap.values.first()).orElseThrow()
//            show.rankings = rankingMap
//            showRepository.save(show)
            // 각 공연 ID에 대해 랭킹 데이터 업데이트
            rankingMap.forEach { (rank, showId) ->
                val show = showRepository.findById(showId).orElseThrow {
                    throw CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND)
                }
                show.rankings[rank] = showId
                showRepository.save(show)

            }

            // Redis 초기화(랭킹)
            redisTemplate.delete(rankingKey)
        }

        // 두 번 째 요구사항 : 응답 속도를 높이는 방안 -> HashSet으로 캐시 저장
//    fun incrementViewCountUsingHash(showId: UUID) {
//        val hashKey = "show:views"
//        redisTemplate.opsForHash<String, Int>().increment(hashKey,showId.toString(), 1)
//    }

    }
}