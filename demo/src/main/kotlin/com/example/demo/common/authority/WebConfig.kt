package com.example.demo.common.authority

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        // 루트 경로("/")로 요청이 들어오면 "login" 뷰(login.html)를 반환합니다.
        registry.addViewController("/").setViewName("index")
    }
}