package com.sparta.interparty.global.redis

import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class RedisLockServiceImpl(
    private val redisTemplate: StringRedisTemplate
) : RedisLockService {

    override fun acquireLock(key: String, expiration: Long): String? {
        val lockId = UUID.randomUUID().toString() // 고유 Lock ID(UUID) 생성
        val result = redisTemplate.opsForValue().setIfAbsent(
            key,
            lockId,
            Duration.ofSeconds(expiration) // Long -> Duration 변환
        )
        if (result == true) log.debug("Lock acquired: ${lockId.substring(0, 4)}...")
        return if (result == true) lockId else null // Lock 성공 시 lockId 반환, 실패 시 null
    }

    override fun releaseLock(key: String, lockId: String) {
        val currentValue = redisTemplate.opsForValue().get(key)
        if (currentValue == lockId) { // Lock 을 설정한 주체인지 확인
            redisTemplate.delete(key) // Lock 해제
            log.debug("Lock released: ${lockId.substring(0, 4)}...")
        }
    }
}
