package com.example.demo.common.exception

class InvalidInputException (
    //fieldName이라는 프로퍼티 가짐
    val fieldName:String="",
    message: String="Invalid Input"
    //RuntimeException을 상속받고 message 넘겨줌
): RuntimeException(message)