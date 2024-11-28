package com.sparta.interparty.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/*
 * Redis와 Spring Boot 간의 연동을 설정하는 클래스입니다.
 * - RedisTemplate을 통해 Redis와 데이터를 읽고 쓰는 데 필요한 설정을 제공합니다.
 */
@Configuration
class RedisConfig {

    /**
     * RedisTemplate을 설정하는 메서드입니다.
     * - RedisTemplate은 Redis와 데이터를 주고받기 위해 사용됩니다.
     * - 키와 값을 직렬화/역직렬화하여 Redis에 저장 가능한 형식으로 변환합니다.
     *
     * @param connectionFactory Redis 서버와 연결을 관리하는 RedisConnectionFactory입니다.
     * @return RedisTemplate<String, Any>: Redis에서 키는 문자열(String), 값은 객체(Object)로 처리합니다.
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        // Redis 서버와의 연결 정보 설정
        template.setConnectionFactory(connectionFactory)

        // Key를 문자열 형식으로 직렬화
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        // Value(값)를 JSON 형식으로 직렬화
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        // 설정 완료된 RedisTemplate 반환
        return template
    }
}