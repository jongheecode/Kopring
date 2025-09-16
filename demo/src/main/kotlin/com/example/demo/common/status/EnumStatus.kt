package com.example.demo.common.status

//공통 상태값/코드 Enum 정의
enum class Gender(val desc: String){
    MAN("남"),
    WOMAN("여")
}
enum class ResultCode(val msg: String){
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생했습니다.")
}
enum class ROLE{    
    MEMBER
}   