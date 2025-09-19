package com.example.demo.common.authority

import com.example.demo.member.dto.LoginResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.util.Date
import java.security.Key

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}") // <- 여기에 맞게 수정했습니다.
    private lateinit var secretKey: String

    @Value("\${jwt.token.access-expiration-time}")
    private var accessExpirationTime: Long = 0

    @Value("\${jwt.token.refresh-expiration-time}")
    private var refreshExpirationTime: Long = 0

    private val key: Key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    // 수정: Authentication 객체 대신 String loginId와 String role을 받도록 변경
    fun createToken(loginId: String, role: String): LoginResponse {
        val now = Date().time
        val accessToken = Jwts.builder()
            .setSubject(loginId)
            .claim("role", role)
            .setExpiration(Date(now + accessExpirationTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(loginId)
            .setExpiration(Date(now + refreshExpirationTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessExpirationTime
        )
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val principal = claims.subject
        val role = claims["role"] as String
        val authorities = listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_${role}"))

        return UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}