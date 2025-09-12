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
    private val _loginId:String?, //로그인 아이디

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요."
    )
    @JsonProperty("password")
    private val _password:String?, //비밀번호, 영문/숫자/특수문자를 포함한 8~20자리

    @field:NotBlank
    @JsonProperty("name")
    private val _name:String?,     //이름

    @field:NotBlank
    @field:Pattern(
        regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
        message = "날짜형식(YYYY-DD-MM)을 확인해주세요."
    )
    @JsonProperty("birthDate")
    private val _birthDate: String?,  //생년월일, YYYY-MM-DD 형식

    @field:NotBlank
    @field:ValidEnum(enumClass = Gender::class, message = "MAN 이나 WOMAN 중 하나를 선택해주세요.")
    @JsonProperty("gender")
    private val _gender: String?, //성별,MAN(남) 이나 WOMAN(여) 중 하나

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    private val _email: String?,  //이메일,이메일 형식
    ){

    val loginId:String //로그인 아이디
        get() = _loginId!!
    val password:String //비밀번호, 영문/숫자/특수문자를 포함한 8~20자리
        get() = _password!!
    val name:String     //이름
        get() = _name!!
    val birthDate: LocalDate  //생년월일, YYYY-MM-DD 형식
        get() = _birthDate!!.toLocalDate()
    val gender: Gender //성별,MAN(남) 이나 WOMAN(여) 중 하나
        get() = Gender.valueOf(_gender!!)
    val email: String  //이메일,이메일 형식
        get() = _email!!

    //확장함수 : String.toLocalDate 함수 -> String을 yyyy-MM-dd 타입으로 변환해줌
    private fun String.toLocalDate(): LocalDate=
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    fun toEntity(): Member=
        //엔티티 반환 함수
        Member(id,loginId,password,name, birthDate, gender,email)
}