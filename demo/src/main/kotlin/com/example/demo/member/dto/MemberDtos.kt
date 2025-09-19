package com.example.demo.member.dto

import com.example.demo.common.annotation.ValidEnum
import com.example.demo.common.status.Gender
import com.example.demo.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 회원가입 요청 객체
data class MemberDtoRequest (
    val id: Long?,

    @field:NotBlank
    @JsonProperty("loginId")
    val loginId:String?,

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요."
    )
    @JsonProperty("password")
    val password:String?,

    @field:NotBlank
    @JsonProperty("name")
    val name:String?,

    @field:NotBlank
    @field:Pattern(
        regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
        message = "날짜형식(YYYY-DD-MM)을 확인해주세요."
    )
    @JsonProperty("birthDate")
    val birthDate: String?,

    @field:NotBlank
    @field:ValidEnum(enumClass = Gender::class, message = "MAN 이나 WOMAN 중 하나를 선택해주세요.")
    @JsonProperty("gender")
    val gender: String?,

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    val email: String?
) {
    fun toEntity(): Member=
        Member(id, loginId!!, password!!, name!!, birthDate!!.toLocalDate(), Gender.valueOf(gender!!), email!!)

    private fun String.toLocalDate(): LocalDate=
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

// 로그인 요청 DTO
data class LoginRequest(
    @field:NotBlank
    @JsonProperty("loginId")
    val loginId: String,

    @field:NotBlank
    @JsonProperty("password")
    val password: String
)

// 로그인 응답 DTO (토큰 정보 포함)
data class LoginResponse(
    @JsonProperty("accessToken")
    val accessToken: String,

    @JsonProperty("refreshToken")
    val refreshToken: String,

    @JsonProperty("tokenType")
    val tokenType: String = "Bearer",

    @JsonProperty("expiresIn")
    val expiresIn: Long
)

// 회원 정보 응답 DTO
data class MemberResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("loginId")
    val loginId: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("birthDate")
    val birthDate: String,

    @JsonProperty("gender")
    val gender: String,

    @JsonProperty("email")
    val email: String
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id!!,
                loginId = member.loginId,
                name = member.name,
                birthDate = member.birthDate.toString(),
                gender = member.gender.name,
                email = member.email
            )
        }
    }
}