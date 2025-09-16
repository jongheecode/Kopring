package com.example.demo.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter // import 변경

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() { // GenericFilterBean 대신 OncePerRequestFilter 상속

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 토큰이 필요 없는 URL은 필터를 건너뜁니다.
        val path = request.requestURI
        val excludedUrls = listOf(
            "/", "/home", "/login", "/signup",
            "/api/member/signup", "/api/member/login"
        )
        // 정적 리소스 URL도 예외 처리
        val staticResources = listOf(
            "/css/", "/js/", "/images/", "/favicon.ico"
        )

        // URL이 예외 목록에 포함되면 다음 필터로 바로 넘어갑니다.
        if (excludedUrls.any { path == it } || staticResources.any { path.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}