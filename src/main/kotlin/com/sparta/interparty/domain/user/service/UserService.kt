package com.sparta.interparty.domain.user.service

import com.sparta.interparty.domain.user.dto.req.UpdateUserReqDto
import com.sparta.interparty.domain.user.dto.res.UserResDto
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.repo.UserRepository
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import com.sparta.interparty.global.security.UserDetailsImpl
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private final val userRepository: UserRepository,
    private final val passwordEncoder: PasswordEncoder
) {

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

    @Transactional
    fun signout(userDetails: UserDetailsImpl, password: String) {
        if (!passwordEncoder.matches(password, userDetails.password)) {
            throw CustomException(ExceptionResponseStatus.INVALID_PASSWORD)
        }
        val user = userDetails.getUser() ?: throw CustomException(ExceptionResponseStatus.USER_NOT_FOUND)
        user.isDeleted = true
        userRepository.save(user)
    }

    @Transactional
    fun updateUserInfo(userDetails: UserDetailsImpl, req: UpdateUserReqDto) {
        if (!passwordEncoder.matches(req.currentPassword, userDetails.password)) {
            throw CustomException(ExceptionResponseStatus.INVALID_PASSWORD)
        }
        val user = userDetails.getUser() ?: throw CustomException(ExceptionResponseStatus.USER_NOT_FOUND)
        setUserInfo(req, user)
        userRepository.save(user)
    }

    private fun setUserInfo(req: UpdateUserReqDto, user: User) {
        var isModified = false
        req.newPassword?.let { user.password = passwordEncoder.encode(it); isModified = true }
        req.email?.let { user.email = it; isModified = true }
        req.nickname?.let { user.nickname = it; isModified = true }
        req.phoneNumber?.let { user.phoneNumber = it; isModified = true }
        if (!isModified) {
            throw CustomException(ExceptionResponseStatus.INVALID_UPDATE_REQUEST)
        }
    }
}