package com.example.demo.member.service

import com.example.demo.common.authority.JwtTokenProvider
import com.example.demo.common.authority.TokenInfo
import com.example.demo.common.exception.InvalidInputException
import com.example.demo.common.status.ROLE
import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.LoginResponse
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.dto.MemberResponse
import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


//핵심 도메인 로직, 트랜잭션 경계,예외 처리
@Transactional
@Service
class MemberService (
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
){
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        //ID 중복 검사
        var member: Member?=memberRepository.findByLoginId(memberDtoRequest.loginId)
        if(member!=null){
            throw InvalidInputException("loginId","이미 등록된 ID 입니다.")
        }
        member=memberDtoRequest.toEntity()
        //비밀번호 암호화
        member.password = passwordEncoder.encode(member.password)
        memberRepository.save(member)

        val memberRole: MemberRole= MemberRole(null, ROLE.MEMBER,member)
        memberRoleRepository.save(memberRole)
        return "회원가입이 완료되었습니다."
    }

    /**
     * 로그인 - JWT 토큰 발행
     */
    fun login(loginRequest: LoginRequest): LoginResponse {
        //회원 조회
        val member = memberRepository.findByLoginId(loginRequest.loginId)
            ?: throw InvalidInputException("loginId", "존재하지 않는 아이디입니다.")
        
        //비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw InvalidInputException("password", "비밀번호가 일치하지 않습니다.")
        }
        
        //JWT 토큰 생성
        val tokenInfo = jwtTokenProvider.generateToken(member.id!!, member.loginId)
        
        return LoginResponse(
            accessToken = tokenInfo.accessToken,
            refreshToken = tokenInfo.refreshToken,
            tokenType = "Bearer",
            expiresIn = tokenInfo.accessTokenExpirationTime
        )
    }

    /**
     * 내 정보 조회
     */
    fun getMyInfo(memberId: Long): MemberResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { InvalidInputException("memberId", "존재하지 않는 회원입니다.") }
        
        return MemberResponse.from(member)
    }

    /**
     * 내 정보 수정
     */
    fun updateMyInfo(memberId: Long, updateRequest: MemberDtoRequest): String {
        val member = memberRepository.findById(memberId)
            .orElseThrow { InvalidInputException("memberId", "존재하지 않는 회원입니다.") }
        
        //이름, 생년월일, 성별, 이메일만 수정 가능 (로그인ID, 비밀번호는 별도 처리)
        member.name = updateRequest.name
        member.birthDate = updateRequest.birthDate
        member.gender = updateRequest.gender
        member.email = updateRequest.email
        
        memberRepository.save(member)
        return "회원 정보가 수정되었습니다."
    }
        
}