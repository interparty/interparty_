package com.sparta.interparty.global.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.sparta.interparty.domain.auth.dto.req.SigninReqDto
import com.sparta.interparty.global.security.UserDetailsImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException

class JwtAuthenticationFilter(private val jwtUtil: JwtUtil) : UsernamePasswordAuthenticationFilter() {
    init {
        setFilterProcessesUrl("/api/auth/signin")
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {
        log.info("로그인 시도")
        try {
            val requestDto: SigninReqDto = ObjectMapper().readValue(
                req.inputStream,
                SigninReqDto::class.java
            )

            return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    requestDto.username,
                    requestDto.password,
                    null
                )
            )
        } catch (e: IOException) {
            log.error(e.message)
            throw RuntimeException(e.message)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        log.info("로그인 성공 및 JWT 생성")
        val userDetails = authResult.principal as UserDetailsImpl
        val userId = userDetails.getUser()!!.id
        val username = userDetails.getUser()!!.username
        val email = userDetails.getUser()!!.email
        val userRole = userDetails.getUser()!!.userRole
        val nickname = userDetails.getUser()!!.nickname

        val token = jwtUtil.createToken(userId, username, email, userRole.toString(), nickname)
        //        res.addHeader("Authorization", token);
        jwtUtil.addJwtToCookie(token, res)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.info("로그인 실패")
        res.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}