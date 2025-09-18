package com.example.demo.controller

import com.example.demo.common.dto.BaseResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping("/health")
    fun health(): BaseResponse<String> {
        return BaseResponse(
            data = "OK",
            message = "서버 상태 정상"
        )
    }
}
