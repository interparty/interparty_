package com.sparta.interparty.global.redis

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class RedisLockServiceImplTest(@Autowired private val redisLockService: RedisLockService) {

    @Test
    @DisplayName("Redis Lock 획득 및 해제 테스트")
    fun acquireAndReleaseTest() {
        // 정상 Lock 획득
        val testKey = "test:lock"
        val lockId = redisLockService.acquireLock(testKey, 10)
        assertNotNull(lockId); lockId!!

        // Lock 추가 획득 불가
        val concurrentId = redisLockService.acquireLock(testKey, 10)
        assertNull(concurrentId)

        // Lock 해제 실패
        var invalidKey: String
        do { invalidKey = UUID.randomUUID().toString() } while (invalidKey == testKey)
        redisLockService.releaseLock(invalidKey, lockId)
        val failedId = redisLockService.acquireLock(testKey, 10)
        assertNull(failedId)

        // Lock 해제 성공
        redisLockService.releaseLock(testKey, lockId)
        val successId = redisLockService.acquireLock(testKey, 10)
        assertNotNull(successId)
    }
}