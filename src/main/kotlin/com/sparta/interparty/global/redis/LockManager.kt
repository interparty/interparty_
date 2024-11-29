package com.sparta.interparty.global.redis

import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantLock

@Component
class LockManager {
    private val lockMap = mutableMapOf<String, ReentrantLock>()

    fun getLock(key: String): ReentrantLock {
        return lockMap.computeIfAbsent(key) { ReentrantLock() }
    }
}
