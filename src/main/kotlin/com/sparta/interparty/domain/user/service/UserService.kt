package com.sparta.interparty.domain.user.service

import com.sparta.interparty.domain.user.dto.res.UserResDto
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.stereotype.Service

@Service
class UserService {
    fun getUserInfo(userDetails: UserDetailsImpl): UserResDto {
        if (userDetails.getUser()!!.isDeleted) {
            throw CustomException(ExceptionResponseStatus.DELETED_USER)
        }

//        val id: Long,               // 유저의 고유 ID
//        val username: String,       // 유저 이름
//        val email: String,          // 이메일
//        val nickname: String,       // 닉네임
//        val phoneNumber: String,    // 전화번호
//        val userRole: String        // 유저 역할 (USER, ADMIN 등)
        val user = requireNotNull(userDetails.getUser()) { "User cannot be null" }
        return UserResDto(
            id = user.id,
            username = user.username,
            email = user.email,
            nickname = user.nickname,
            phoneNumber = user.phoneNumber,
            userRole = user.userRole.toString()
        )
    }
}