package com.example.demo.controller

import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

/**
 * 웹 페이지 컨트롤러 (Thymeleaf 템플릿 사용)
 */
@Controller
class WebController(
    private val memberService: MemberService
) {
    /**
     * 루트 페이지 (index.html)
     */
    @GetMapping("/")
    fun home(): String {
        return "index"
    }

    /**
     * 홈 페이지
     */
    @GetMapping("/home")
    fun homePage(model: Model): String {
        model.addAttribute("message", "Spring Boot 서버가 정상적으로 실행 중입니다!")
        return "index"
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    fun loginPage(model: Model, error: String?): String {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.")
        }
        return "login"
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/signup")
    fun signupPage(model: Model): String {
        model.addAttribute("memberDtoRequest", MemberDtoRequest(
            id = null,
            loginId = null, // 변경: 밑줄 제거
            password = null, // 변경: 밑줄 제거
            name = null,
            birthDate = null,
            gender = null,
            email = null
        ))
        return "signup"
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/signup")
    fun signup(
        @Valid memberDtoRequest: MemberDtoRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            return "signup"
        }

        try {
            memberService.signUp(memberDtoRequest)
            model.addAttribute("success", "회원가입이 완료되었습니다!")
            return "login" // 회원가입 성공 시 로그인 페이지로 이동
        } catch (e: Exception) {
            model.addAttribute("error", e.message)
            return "signup"
        }
    }
}