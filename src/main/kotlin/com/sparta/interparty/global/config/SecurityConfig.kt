package com.sparta.interparty.global.config

import com.sparta.interparty.global.security.UserDetailsServiceImpl
import com.sparta.interparty.global.util.JwtAuthenticationFilter
import com.sparta.interparty.global.util.JwtAuthorizationFilter
import com.sparta.interparty.global.util.JwtUtil
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsServiceImpl,
    private val authenticationConfiguration: AuthenticationConfiguration
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        val filter = JwtAuthenticationFilter(jwtUtil)
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration))
        return filter
    }

    @Bean
    fun jwtAuthorizationFilter(): JwtAuthorizationFilter {
        return JwtAuthorizationFilter(jwtUtil, userDetailsService)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.authorizeHttpRequests {
            it.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입 요청 접근 허용
//        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한 접근 허용
                .anyRequest().authenticated() // 그 외 모든 요청 인증 처리
        }

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter::class.java)
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}