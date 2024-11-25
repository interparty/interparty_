package com.sparta.interparty.global.exception

import jodd.net.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 처리되지 않은 모든 RuntimeException 상속체에 대한 처리
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ExceptionResponse> {
        return ExceptionResponseStatus.INTERNAL_SERVER_ERROR.toResponseEntity()
    }

    // 사용자 지정 예외 클래스에 대한 처리
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ExceptionResponse> {
        return ex.exceptionResponseStatus.toResponseEntity()
    }

    // 유효성 검사 예외의 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
        val msg: String = ex.bindingResult.allErrors[0].defaultMessage?: "입력 값이 유효하지 않습니다."
        return ExceptionResponseStatus.BAD_REQUEST.toResponseEntity(msg)
    }
}