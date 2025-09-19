package com.example.demo.member.controller

import com.example.demo.common.authority.CustomUserDetails
import com.example.demo.common.dto.BaseResponse
import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.dto.MemberResponse
import com.example.demo.member.dto.LoginResponse
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


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

    /**
     * 로그인 - JWT 토큰 발행
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequest: LoginRequest): BaseResponse<LoginResponse> {
        val loginResponse = memberService.login(loginRequest)
        return BaseResponse(data = loginResponse, message = "로그인 성공")
    }

    /**
     * 내 정보 조회 (JWT 토큰 필요)
     */
    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): BaseResponse<MemberResponse> {
        val userDetails = authentication.principal as CustomUserDetails
        val memberId = userDetails.getMemberId() //CustomUserDetails에서 회원 ID 추출
        val memberResponse = memberService.getMyInfo(memberId)
        return BaseResponse(data = memberResponse, message = "내 정보 조회 성공")
    }

    /**
     * 내 정보 수정 (JWT 토큰 필요)
     */
    @PutMapping("/me")
    fun updateMyInfo(
        authentication: Authentication,
        @RequestBody @Valid updateRequest: MemberDtoRequest
    ): BaseResponse<Unit> {
        val userDetails = authentication.principal as CustomUserDetails
        val memberId = userDetails.getMemberId() //CustomUserDetails에서 회원 ID 추출
        val resultMsg = memberService.updateMyInfo(memberId, updateRequest)
        return BaseResponse(message = resultMsg)
    }
}