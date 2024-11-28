package com.sparta.interparty.global.aop

import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import com.sparta.interparty.global.redis.RedisLockService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LockAspect(
    private val redisLockService: RedisLockService // Lettuce 기반 RedisLockService 주입
) {
    @Around("@annotation(lock)") // @Lock 애노테이션이 붙은 메서드에 적용
    fun around(joinPoint: ProceedingJoinPoint, lock: Lock): Any? {
        val key = lock.key
        val expiration = lock.expiration

        // Redis Lock 획득
        val lockId = redisLockService.acquireLock(key, expiration)
            ?: throw CustomException(ExceptionResponseStatus.DUPLICATE_RESERVATION)

        try {
            // 실제 비즈니스 로직 실행
            return joinPoint.proceed()
        } finally {
            // Lock 해제
            redisLockService.releaseLock(key, lockId)
        }
    }
}
