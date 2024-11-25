package com.sparta.interparty.domain.auth.controller

import com.sparta.interparty.domain.auth.dto.req.SignupReqDto
import com.sparta.interparty.domain.auth.dto.res.SignupResDto
import com.sparta.interparty.domain.auth.service.AuthService
import jakarta.validation.Valid
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
    fun signup(@Valid @RequestBody signupRequestDto: SignupReqDto): ResponseEntity<SignupResDto> {
        authService.signup(signupRequestDto)
        val res = SignupResDto(okSignup = "회원 가입이 완료되었습니다!")
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }
}