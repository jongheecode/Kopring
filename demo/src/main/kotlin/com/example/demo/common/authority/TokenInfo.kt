package com.example.demo.common.authority

data class TokenInfo(
    val grantType: String, //토큰 타입
    val accessToken: String, //액세스 토큰
)
