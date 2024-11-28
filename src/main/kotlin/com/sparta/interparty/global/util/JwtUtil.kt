package com.sparta.interparty.global.util

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val log: Logger = LoggerFactory.getLogger(JwtUtil::class.java)

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 60 * 60 * 1000L // 60분
    }

    @PostConstruct
    fun init() {
        if (secretKey.isNullOrBlank()) {
            throw IllegalArgumentException("JWT Secret Key is not configured")
        }
        try {
            log.info("Initializing JWT Secret Key: $secretKey") // 디버깅용 로그
            /*val bytes = Base64.getDecoder().decode(secretKey)
            key = Keys.hmacShaKeyFor(bytes)*/
            key = Keys.secretKeyFor(signatureAlgorithm)
        } catch (e: IllegalArgumentException) {
            log.error("Invalid Base64 JWT Secret Key: ${e.message}")
            throw IllegalArgumentException("Invalid JWT Secret Key", e)
        }

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
        if (token.isNullOrEmpty()) return

        try {
            val encodedToken = URLEncoder.encode(token, "utf-8")
                .replace("\\+".toRegex(), "%20") // 공백 인코딩
            val cookie = Cookie("Authorization", encodedToken)
            cookie.path = "/"
            cookie.isHttpOnly = true
            res.addCookie(cookie)
        } catch (e: UnsupportedEncodingException) {
            log.error("Failed to encode JWT token for cookie: ${e.message}")
        }
    }

    fun getTokenFromRequest(req: HttpServletRequest): String? {
        val cookies = req.cookies
        cookies?.forEach { cookie ->
            if (cookie.name == "Authorization") {
                return try {
                    URLDecoder.decode(cookie.value, "UTF-8") // Decode
                } catch (e: UnsupportedEncodingException) {
                    log.error("Failed to decode JWT token from cookie: ${e.message}")
                    null
                }
            }
        }
        return null
    }

    fun extractClaims(token: String?): Claims {
        if (token.isNullOrEmpty()) {
            throw IllegalArgumentException("JWT token is missing")
        }
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: SecurityException) {
            log.error("Invalid JWT signature: ${e.message}")
            false
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT structure: ${e.message}")
            false
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT token: ${e.message}")
            false
        } catch (e: UnsupportedJwtException) {
            log.error("Unsupported JWT token: ${e.message}")
            false
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: ${e.message}")
            false
        }
    }
}