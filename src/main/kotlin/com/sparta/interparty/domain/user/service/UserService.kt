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