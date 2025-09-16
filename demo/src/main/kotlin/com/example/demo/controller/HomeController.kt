package com.example.demo.controller

import com.example.demo.common.dto.BaseResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @GetMapping("/")
    fun home(): BaseResponse<String> {
        return BaseResponse(
            data = "Spring Boot 서버가 정상적으로 실행 중입니다!",
            message = "서버 연결 성공"
        )
    }

    @GetMapping("/health")
    fun health(): BaseResponse<String> {
        return BaseResponse(
            data = "OK",
            message = "서버 상태 정상"
        )
    }
}
