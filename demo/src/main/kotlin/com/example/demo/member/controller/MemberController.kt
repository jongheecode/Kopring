package com.example.demo.member.controller

import com.example.demo.common.dto.BaseResponse
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


//HTTP 요청 수신, 입력 검증, 서비스 호출, 응답 매핑
@RequestMapping("/api/member")
@RestController
class MemberController( 
    private val memberService: MemberService
) {
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUP(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        val resultMsg: String=memberService.signUp(memberDtoRequest) 
        return BaseResponse(message = resultMsg)
    }

}