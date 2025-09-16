package com.example.demo.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            // JWT 인증 방식은 무상태(stateless)이므로 세션을 사용하지 않습니다.
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/", "/home", "/login", "/signup",
                    "/api/member/signup", "/api/member/login",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll() // 특정 경로(로그인/회원가입 등)는 인증 없이 접근을 허용합니다.
                .anyRequest().authenticated() // 그 외의 모든 요청은 JWT 토큰 인증이 필요합니다.
            }
            // 기존 `formLogin` 설정을 제거합니다.
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build() // 최종 필터 체인 구성
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder() // 다양한 비밀번호 인코딩 방식을 지원합니다.
}