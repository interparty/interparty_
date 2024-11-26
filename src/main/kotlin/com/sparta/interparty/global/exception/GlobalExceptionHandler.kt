package com.sparta.interparty.global.exception

import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
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

    // NullPointer 예외에 대한 처리 (서비스 로직의 결함, 오류 로그 생성)
    @ExceptionHandler(NullPointerException::class)
    protected fun handleNullPointerException(ex: NullPointerException): ResponseEntity<ExceptionResponse> {
        log.error("handleNullPointerException", ex)
        return ExceptionResponse.toResponseEntityWith(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 존재하지 않는 값을 참조하였습니다.")
    }

    // 요청의 바디가 없을 때의 처리
    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse>? {
        return ExceptionResponseStatus.BODY_NOT_FOUND.toResponseEntity()
    }

    // 사용자 지정 예외 클래스에 대한 처리
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ExceptionResponse> {
        return ex.exceptionResponseStatus.toResponseEntity()
    }

    // 유효성 검사 예외의 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionGroupResponse> {
        val res = ExceptionGroupResponse(HttpStatus.BAD_REQUEST)
        ex.bindingResult.allErrors.forEach {
            res.addMessage((it as FieldError).field, it.defaultMessage ?: "입력 값이 유효하지 않습니다.")
        }
        return ResponseEntity.badRequest().body(res)
    }

}