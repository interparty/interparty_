package com.sparta.interparty.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class ExceptionResponseStatus(val status: HttpStatus, val message: String) {

    // global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 오류가 발생하였습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    RESPONSE_ERROR_A(HttpStatus.BAD_REQUEST, "오류 응답 예시 A"),
    RESPONSE_ERROR_B(HttpStatus.BAD_REQUEST, "오류 응답 예시 B"),

    // auth
    DUPLICATE_USERNAME(HttpStatus.CONFLICT,"이미 존재하는 계정 이름입니다."), // 409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_USERROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 권한입니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
  
    // reservation

    // review

    // show
    MANAGER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "매니저 권한이 필요한 서비스입니다."), // 403
    SHOW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연 정보를 찾을 수 없습니다."), // 404
    NO_FIELD_MODIFIED(HttpStatus.BAD_REQUEST, "수정할 필드가 없습니다."), // 400
    INVALID_FIELD_MODIFICATION(HttpStatus.BAD_REQUEST, "잘못된 값으로 수정을 시도중입니다."), // 400
    NOT_A_MANAGER(HttpStatus.FORBIDDEN, "해당 공연의 매니저가 아닙니다."), // 403

    // user
    DELETED_USER(HttpStatus.FORBIDDEN,"이미 탈퇴한 사용자입니다.")
    ;
    // 열거형에서 바로 응답 엔티티 생성
    fun toResponseEntity(): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(this.status).body(ExceptionResponse(this))
    }

    // 열거형에서 메시지를 오버라이딩하여 응답 엔티티 생성
    fun toResponseEntity(message: String): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(this.status).body(ExceptionResponse(this.status, message))
    }
}