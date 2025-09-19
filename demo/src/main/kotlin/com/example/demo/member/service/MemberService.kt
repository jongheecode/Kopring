package com.example.demo.member.service

import com.example.demo.common.authority.JwtTokenProvider
import com.example.demo.common.exception.InvalidInputException
import com.example.demo.common.exception.PasswordMismatchException
import com.example.demo.common.status.Gender
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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        if (memberRepository.existsByLoginId(memberDtoRequest.loginId!!)) {
            throw InvalidInputException("loginId", "이미 존재하는 아이디입니다.")
        }
        val encodedPassword = passwordEncoder.encode(memberDtoRequest.password)
        val member = Member(
            loginId = memberDtoRequest.loginId,
            password = encodedPassword,
            name = memberDtoRequest.name!!,
            birthDate = LocalDate.parse(memberDtoRequest.birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            gender = Gender.valueOf(memberDtoRequest.gender!!),
            email = memberDtoRequest.email!!
        )

        memberRepository.save(member)
        memberRoleRepository.save(MemberRole(member = member, role = ROLE.MEMBER))

        return "회원가입이 완료되었습니다."
    }

    fun login(loginRequest: LoginRequest): LoginResponse {
        val member = memberRepository.findByLoginId(loginRequest.loginId)
            ?: throw InvalidInputException("loginId", "존재하지 않는 아이디입니다.")
        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw PasswordMismatchException("password", "비밀번호가 일치하지 않습니다.")
        }

        val memberRoles = memberRoleRepository.findByMember(member)
        val role = memberRoles.firstOrNull()?.role?.name ?: throw InvalidInputException("role", "회원 권한 정보가 없습니다.")

        val tokenInfo = jwtTokenProvider.createToken(member.loginId, role)
        return LoginResponse(
            accessToken = tokenInfo.accessToken,
            refreshToken = tokenInfo.refreshToken,
            expiresIn = tokenInfo.expiresIn
        )
    }

    fun getMyInfo(memberId: Long): MemberResponse {
        val member = memberRepository.findByIdOrNull(memberId) ?: throw InvalidInputException("id", "존재하지 않는 회원입니다.")
        return MemberResponse.from(member)
    }

    fun updateMyInfo(memberId: Long, updateRequest: MemberDtoRequest): String {
        val member = memberRepository.findByIdOrNull(memberId) ?: throw InvalidInputException("id", "존재하지 않는 회원입니다.")

        member.password = passwordEncoder.encode(updateRequest.password!!)
        member.name = updateRequest.name!!
        member.birthDate = LocalDate.parse(updateRequest.birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        member.gender = Gender.valueOf(updateRequest.gender!!)
        member.email = updateRequest.email!!

        memberRepository.save(member)

        return "내 정보가 수정되었습니다."
    }
}