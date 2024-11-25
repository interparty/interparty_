package com.sparta.interparty.domain.auth.controller

import com.sparta.interparty.domain.auth.dto.req.SignupRequestDto
import com.sparta.interparty.domain.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody signupRequestDto: SignupRequestDto): ResponseEntity<String> {
        authService.signup(signupRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입이 완료되었습니다!")
    }
}