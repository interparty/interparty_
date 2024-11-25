package com.sparta.interparty.global.util

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.Key
import java.util.*

@Component
class JwtUtil {
    @Value("\${jwt.secret.key}")
    private val secretKey: String? = null
    private var key: Key? = null
    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun createToken(userId: UUID, username: String, email: String, userRole: String, nickname: String): String {
        val date = Date()

        return BEARER_PREFIX +
                Jwts.builder()
                    .setSubject(username)
                    .claim("id", userId)
                    .claim("email", email)
                    .claim("userRole", userRole)
                    .claim("nickname", nickname)
                    .setExpiration(Date(date.time + TOKEN_TIME))
                    .setIssuedAt(date) // 발급일
                    .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                    .compact()
    }

    fun addJwtToCookie(token: String?, res: HttpServletResponse) {
        var token = token
        try {
            token = URLEncoder.encode(token, "utf-8")
                .replace("\\+".toRegex(), "%20") // Cookie Value 에는 공백이 불가능해서 encoding 진행

            val cookie = Cookie("Authorization", token) // Name-Value
            cookie.path = "/"

            // Response 객체에 Cookie 추가
            res.addCookie(cookie)
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message)
        }
    }

    fun getTokenFromRequest(req: HttpServletRequest): String? {
        val cookies = req.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "Authorization") {
                    return try {
                        URLDecoder.decode(cookie.value, "UTF-8") // Encode 되어 넘어간 Value 다시 Decode
                    } catch (e: UnsupportedEncodingException) {
                        null
                    }
                }
            }
        }
        return null
    }

    fun extractClaims(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    // JWT 검증
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.")
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.")
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.")
        } catch (e: UnsupportedJwtException) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.")
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.")
        }
        return false
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 60 * 60 * 1000L // 60분
    }
}