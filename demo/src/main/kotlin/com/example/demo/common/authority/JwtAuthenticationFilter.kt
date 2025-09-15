package com.example.demo.common.authority

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.FilterChain
import org.springframework.web.filter.GenericFilterBean
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilter(

    private val jwtTokenProvider: JwtTokenProvider

) :GenericFilterBean() { //GenericFilterBean : 필터 기능을 구현하기 위한 클래스
    override fun doFilter(request: ServletRequest?,response: ServletResponse?, chain: FilterChain?) {
        val token=resolveToken(request as HttpServletRequest)

        if(token!=null && jwtTokenProvider.validateToken(token)){
            val authentication=jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication=authentication
        }

        chain?.doFilter(request,response)
}
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken=request.getHeader("Authorization")

        return if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            bearerToken.substring(7)
        }else{
            null
        }
    }
}
