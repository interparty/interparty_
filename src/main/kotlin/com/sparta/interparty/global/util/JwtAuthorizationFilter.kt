package com.sparta.interparty.global.util

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthorizationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {

        var tokenValue = jwtUtil.getTokenFromRequest(req)

        /*
        쿠키에 있는 토큰을 검증할 때 bearer을 substring하지 않으면 decode 과정에서 오류가 발생.
        토큰이 만들어지기 전인 null 값일 때 subString이 실행되서 오류가 뜨는 것을 방지
         */
        if (!tokenValue.isNullOrEmpty() && tokenValue.startsWith("Bearer ")) {
            tokenValue = tokenValue!!.substring(7)
        }

        if (StringUtils.hasText(tokenValue)) {
            if (!jwtUtil.validateToken(tokenValue!!)) {
                log.error("Token Error")
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token Error")
                return
            }

            val info = jwtUtil.extractClaims(tokenValue)

            try {
                setAuthentication(info.subject)
            } catch (e: Exception) {
                log.error(e.message)
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token Error")
                return
            }
        }

        filterChain.doFilter(req, res)
    }

    // 인증 처리
    private fun setAuthentication(username: String) {
        val context = SecurityContextHolder.createEmptyContext()
        val authentication = createAuthentication(username)
        context.authentication = authentication

        SecurityContextHolder.setContext(context)
    }

    // 인증 객체 생성
    private fun createAuthentication(username: String): Authentication {
        val userDetails = userDetailsService!!.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }
}