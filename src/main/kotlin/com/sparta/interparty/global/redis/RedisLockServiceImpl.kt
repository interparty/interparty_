package com.sparta.interparty.global.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class RedisLockServiceImpl(
    private val redisTemplate: StringRedisTemplate
) : RedisLockService {

    override fun acquireLock(Key: String, expiration: Long): String? {
        val lockId = UUID.randomUUID().toString() // 고유 Lock ID(UUID) 생성
        val result = redisTemplate.opsForValue().setIfAbsent(
            Key,
            lockId,
            Duration.ofSeconds(expiration) // Long -> Duration 변환
        )
        return if (result == true) lockId else null // Lock 성공 시 lockId 반환, 실패 시 null
    }

    override fun releaseLock(Key: String, lockId: String) {
        val currentValue = redisTemplate.opsForValue().get(Key)
        if (currentValue == lockId) { // Lock을 설정한 주체인지 확인
            redisTemplate.delete(Key) // Lock 해제
        }
    }
}
