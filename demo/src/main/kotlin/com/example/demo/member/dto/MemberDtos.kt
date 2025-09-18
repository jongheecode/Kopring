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

data class MemberDtoRequest (
    val id: Long?,

    @field:NotBlank
    @JsonProperty("loginId")
    val loginId:String?, // 변경: 공개 필드

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요."
    )
    @JsonProperty("password")
    val password:String?, // 변경: 공개 필드

    @field:NotBlank
    @JsonProperty("name")
    val name:String?,     // 변경: 공개 필드

    @field:NotBlank
    @field:Pattern(
        regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
        message = "날짜형식(YYYY-DD-MM)을 확인해주세요."
    )
    @JsonProperty("birthDate")
    val birthDate: String?,  // 변경: 공개 필드

    @field:NotBlank
    @field:ValidEnum(enumClass = Gender::class, message = "MAN 이나 WOMAN 중 하나를 선택해주세요.")
    @JsonProperty("gender")
    val gender: String?, // 변경: 공개 필드

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    val email: String?  // 변경: 공개 필드
) {
    fun toEntity(): Member=
        Member(id, loginId!!, password!!, name!!, birthDate!!.toLocalDate(), Gender.valueOf(gender!!), email!!)

    private fun String.toLocalDate(): LocalDate=
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

// 나머지 DTO들은 그대로 유지
data class LoginRequest(
    @field:NotBlank
    @JsonProperty("loginId")
    val loginId: String,

    @field:NotBlank
    @JsonProperty("password")
    val password: String
)
// ...