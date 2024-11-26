package com.sparta.interparty.domain.user.controller

import com.sparta.interparty.domain.user.dto.req.UserReqDto
import com.sparta.interparty.domain.user.dto.res.UserResDto
import com.sparta.interparty.domain.user.service.UserService
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/info")
    fun getUserInfo(@AuthenticationPrincipal userDetails: UserDetailsImpl): ResponseEntity<UserResDto> {
        val res: UserResDto = userService.getUserInfo(userDetails)
        return ResponseEntity.status(HttpStatus.OK).body<UserResDto>(res)
    }
}