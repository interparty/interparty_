package com.sparta.interparty.global.exception

class CustomException(val exceptionResponseStatus: ExceptionResponseStatus) :
    RuntimeException(exceptionResponseStatus.message)

// auth
class DuplicateUsernameException(val exceptionResponseStatus: ExceptionResponseStatus) :
    RuntimeException(exceptionResponseStatus.message)