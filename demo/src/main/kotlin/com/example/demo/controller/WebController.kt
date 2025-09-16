package com.example.demo.controller

import com.example.demo.common.dto.BaseResponse
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 웹 페이지 컨트롤러 (Thymeleaf 템플릿 사용)
 */
@Controller
@RequestMapping
class WebController(
    private val memberService: MemberService
) {

    /**
     * 홈 페이지
     */
    @GetMapping("/home")
    fun home(model: Model): String {
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
            _loginId = null,
            _password = null,
            _name = null,
            _birthDate = null,
            _gender = null,
            _email = null
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
            val resultMsg = memberService.signUp(memberDtoRequest)
            model.addAttribute("success", resultMsg)
            return "login" // 회원가입 성공 시 로그인 페이지로 이동
        } catch (e: Exception) {
            model.addAttribute("error", e.message)
            return "signup"
        }
    }
}
