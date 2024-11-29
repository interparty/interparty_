package com.sparta.interparty.global.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Lock(
    val key: String,       // Redis Lock 키
    val expiration: Long = 10L // Lock 만료 시간 (초 단위, 기본값 10초)
)
