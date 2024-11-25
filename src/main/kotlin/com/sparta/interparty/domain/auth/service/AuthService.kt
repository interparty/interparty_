package com.sparta.interparty.domain.auth.service

import com.sparta.interparty.domain.auth.dto.req.SignupRequestDto
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.domain.user.repo.UserRepository
import com.sparta.interparty.global.exception.DuplicateEmailException
import com.sparta.interparty.global.exception.DuplicateUsernameException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signup(signupRequestDto: SignupRequestDto) {

        if(userRepository.existsByUsername(signupRequestDto.username)){
            throw DuplicateUsernameException(ExceptionResponseStatus.DUPLICATE_USERNAME)
        }

        if(userRepository.existsByEmail(signupRequestDto.email)){
            throw DuplicateEmailException(ExceptionResponseStatus.DUPLICATE_EMAIL)
        }

        val encodedPassword = passwordEncoder.encode(signupRequestDto.password)

        val userRole = UserRole.of(signupRequestDto.userRole)

        val newUser = User(
            username = signupRequestDto.username,
            password = encodedPassword,
            email = signupRequestDto.email,
            nickname = signupRequestDto.nickname,
            userRole = userRole,
            phoneNumber = signupRequestDto.phoneNumber
        )
        userRepository.save(newUser)
    }

}