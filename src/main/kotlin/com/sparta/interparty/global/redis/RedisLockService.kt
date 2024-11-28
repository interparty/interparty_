package com.sparta.interparty.global.redis

interface RedisLockService {
    fun acquireLock(Key: String, expiration: Long): String?

    fun releaseLock(Key: String, lockId: String)
}