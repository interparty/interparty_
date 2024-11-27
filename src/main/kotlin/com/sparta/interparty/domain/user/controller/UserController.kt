package com.sparta.interparty.domain.user.controller

import com.sparta.interparty.domain.user.dto.req.SignoutReqDto
import com.sparta.interparty.domain.user.dto.req.UpdateUserReqDto
import com.sparta.interparty.domain.user.dto.res.OkResDto
import com.sparta.interparty.domain.user.dto.res.UserResDto
import com.sparta.interparty.domain.user.service.UserService
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getUserInfo(@AuthenticationPrincipal userDetails: UserDetailsImpl): ResponseEntity<UserResDto> {
        val res: UserResDto = userService.getUserInfo(userDetails)
        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @DeleteMapping("/signout")
    fun signout(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody req: SignoutReqDto
    ): ResponseEntity<OkResDto> {
        val password = req.password
        userService.signout(userDetails, password)
        val res = OkResDto(okString = "회원 탈퇴가 완료되었습니다.")
        return ResponseEntity.status(HttpStatus.OK).body(res)
    }

    @PatchMapping("/update")
    fun updateUserInfo(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody req: UpdateUserReqDto
    ): ResponseEntity<OkResDto> {
        userService.updateUserInfo(userDetails, req)
        val res = OkResDto(okString = "회원 정보가 수정되었습니다.")
        return ResponseEntity.status(HttpStatus.OK).body(res)
    }
}