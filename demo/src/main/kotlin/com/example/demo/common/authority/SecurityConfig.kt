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
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }//세션 사용 X
            .authorizeHttpRequests {
                it.requestMatchers("/api/member/signup").anonymous()//url 호출하는 사람은 인증 안된 사람
                    .anyRequest().permitAll() //그 외에는 모두가 접근 가능
            }
            .addFilterBefore( //앞에 있는 필터가 먼저 실행되도록 설정
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

            return http.build() //필터 설정 끝
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder=
        PasswordEncoderFactories.createDelegatingPasswordEncoder() //다양한 비밀번호 인코딩 방식을 유연하게 처리
}