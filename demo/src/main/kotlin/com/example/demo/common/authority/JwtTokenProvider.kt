package com.example.demo.common.authority

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.Date
import io.jsonwebtoken.Claims
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 30 //30분 토큰 만료 시간

@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}") //application.yml에 있는 jwt.secret 값을 가져옴 
    //@Value : 프로퍼티 값을 가져옴, 프로퍼티 값을 가져올 때 사용
    lateinit var secretKey: String //토큰 생성 시 사용할 시크릿 키

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))} //시크릿 키를 사용하여 키 생성

    /**
     * 토큰 생성 (기존 방식)
     */
    fun createToken(authentication: Authentication): TokenInfo {
        val authorities:String=authentication
        .authorities
        .joinToString(",",transform=GrantedAuthority::getAuthority)

        val now=Date()
        val accessExpration=Date(now.time+EXPIRATION_MILLISECONDS)

        //accessToken 생성
        val accessToken=Jwts
        .builder()  //Jwts.builder() : JWT 빌더 생성
        .setSubject(authentication.name)  //subject : 토큰에 담을 정보
        .claim("auth",authorities)  //claim : 토큰에 담을 정보
        .setIssuedAt(now)  //issuedAt : 토큰 발급 시간
        .setExpiration(accessExpration)  //expiration : 토큰 만료 시간
        .signWith(key)  //signWith : 토큰 서명
        .compact()  //compact() : 토큰 생성

        return TokenInfo(grantType="Bearer", accessToken, "", EXPIRATION_MILLISECONDS)  //TokenInfo : 토큰 정보
    }

    /**
     * 회원 ID와 로그인 ID로 토큰 생성 (새로운 방식)
     */
    fun generateToken(memberId: Long, loginId: String): TokenInfo {
        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)
        val refreshExpiration = Date(now.time + EXPIRATION_MILLISECONDS * 2) // 리프레시 토큰은 1시간

        // Access Token 생성
        val accessToken = Jwts.builder()
            .setSubject(memberId.toString()) // 회원 ID를 subject로 사용
            .claim("loginId", loginId) // 로그인 ID를 claim에 저장
            .claim("auth", "ROLE_MEMBER") // 기본 권한
            .setIssuedAt(now)
            .setExpiration(accessExpiration)
            .signWith(key)
            .compact()

        // Refresh Token 생성
        val refreshToken = Jwts.builder()
            .setSubject(memberId.toString())
            .claim("type", "refresh")
            .setIssuedAt(now)
            .setExpiration(refreshExpiration)
            .signWith(key)
            .compact()

        return TokenInfo(
            grantType = "Bearer",
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpirationTime = EXPIRATION_MILLISECONDS
        )
    }

    /**
     * 토큰에서 회원 ID 추출
     */
    fun getMemberIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.subject.toLong()
    }

    /**
     * 토큰에서 로그인 ID 추출
     */
    fun getLoginIdFromToken(token: String): String {
        val claims = getClaims(token)
        return claims["loginId"] as String
    }

    /**
     * 토큰 파싱 -> 토큰에 담긴 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims:Claims=getClaims(token)  //getClaims(token) : 토큰에 담긴 정보 추출

        val auth=claims["auth"]?:throw RuntimeException("잘못된 토큰입니다.")  //auth : 토큰에 담긴 정보

        //권한 정보 추출
        val authorities:Collection<GrantedAuthority> =(auth as String)  //GrantedAuthority : 권한 정보
        .split(",")
        .map { SimpleGrantedAuthority(it) }  //SimpleGrantedAuthority : 권한 정보
        //it : 권한 정보
        
        val principal:UserDetails=User(claims.subject, "", authorities)  //User : 사용자 정보
        //User : 사용자 정보
        //claims.subject : 토큰에 담긴 정보
        //authorities : 권한 정보

        return UsernamePasswordAuthenticationToken(principal, "", authorities)  //UsernamePasswordAuthenticationToken : 사용자 인증 토큰
    }

    /**
     * 토큰 검증
     */
    fun validateToken(token: String): Boolean { //validateToken(token) : 토큰 검증
        try{
            getClaims(token) //getClaims(token) : 토큰에 담긴 정보 추출
            return true //true : 토큰 검증 성공
        }catch(e:Exception){
            when(e){
                is SecurityException -> {} //SecurityException : 보안 예외
                is MalformedJwtException -> {} //MalformedJwtException : 잘못된 토큰 예외
                is ExpiredJwtException -> {} //ExpiredJwtException : 만료된 토큰 예외
                is UnsupportedJwtException -> {} //UnsupportedJwtException : 지원하지 않는 토큰 예외
                is IllegalArgumentException -> {} //IllegalArgumentException : 잘못된 인자 예외
                else -> {} //else : 그 외 예외
            }
            println(e.message) //e.message : 예외 메시지
        }
        return false //false : 토큰 검증 실패
    }



    /**
     * 토큰에 담긴 정보 추출
     */

    private fun getClaims(token: String): Claims = //getClaims(token) : 토큰에 담긴 정보 추출
        Jwts.parserBuilder() //Jwts.parserBuilder() : JWT 파서 빌더 생성
        .setSigningKey(key)  //setSigningKey(key) : 토큰 서명 키 설정   
        .build()  //build() : 파서 생성
        .parseClaimsJws(token)  //parseClaimsJws(token) : 토큰 파싱
        .body  //body : 토큰 본문
}