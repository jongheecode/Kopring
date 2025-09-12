package com.example.demo.common.dto

import com.example.demo.common.status.ResultCode

//Request 결과를 BaseResponse에 담을 예정
data class BaseResponse<T>(
    //결과 코드
    val resultCode: String= ResultCode.SUCCESS.name,
    //결과 반환 값
    val data:T?=null,
    //결과 코드 메시지
    val message: String= ResultCode.SUCCESS.msg,
)