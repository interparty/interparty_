package com.sparta.interparty.global.redis

interface RedisLockService {
    fun acquireLock(key: String, expiration: Long): String?

    fun releaseLock(key: String, lockId: String)
}