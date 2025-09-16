package com.example.demo.common.authority

data class TokenInfo(
    val grantType: String, //토큰 타입
    val accessToken: String, //액세스 토큰
    val refreshToken: String, //리프레시 토큰
    val accessTokenExpirationTime: Long //액세스 토큰 만료 시간
)
