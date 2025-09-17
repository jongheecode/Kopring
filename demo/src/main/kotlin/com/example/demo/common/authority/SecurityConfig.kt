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
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/", "/login", "/signup", "/css/**", "/js/**", "/images/**",
                    "/api/member/signup"
                ).permitAll() // "/"도 인증 없이 접근 허용
                    // 로그인 처리 URL은 Spring Security에 맡깁니다.
                    .requestMatchers("/login", "/api/member/login").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login") // 로그인 페이지의 URL
                    .loginProcessingUrl("/api/member/login") // 로그인 폼이 제출될 URL
                    .defaultSuccessUrl("/home", true) // 로그인 성공 시 이동할 URL
                    .failureUrl("/login?error") // 로그인 실패 시 이동할 URL
                    .permitAll()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder() // 다양한 비밀번호 인코딩 방식을 지원합니다.
}