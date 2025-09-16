package com.example.demo.member.entity

import com.example.demo.common.status.Gender
import com.example.demo.common.status.ROLE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate

//JPA 엔티티 정의(테이블 매핑, 연관관계, 컬럼)
@Entity //JPA에서 관리하는 엔티티를 나타내는 어노테이션(데이터베이스의 행 하나)
@Table(
    //로그인 아이디 중복 방지
    uniqueConstraints = [UniqueConstraint(name="uk_member_login_id", columnNames = ["loginId"])] //제약조건
)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //기본 키 + 키 생성 전략 = AUTO = 적절한 전략
    val id:Long?=null,
    //Column 제약조건 설정 -> nullable = false -> NOT NULL
    @Column(nullable = false, length = 30, updatable = false) //아이디는 변경 X
    val loginId: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @Column(nullable = false, length = 10)
    var name: String,

    @Column(nullable = false)
    //Temporal : TemporalType으로 날짜와 시간 매핑
    @Temporal(TemporalType.DATE)
    var birthDate: LocalDate,   

    @Column(nullable = false, length = 5)
    //Enum 타입 매핑 : String 그대로
    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Column(nullable = false, length = 30)
    var email:String,
){
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member") //멤버와 권한을 일대다로 연결
    val memberRole: List<MemberRole>?=null
}

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long?=null,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: ROLE,

    @ManyToOne(fetch = FetchType.LAZY) //권한과 멤버를 다대일로 연결
    @JoinColumn(foreignKey = ForeignKey(name="fk_member_role_member_id"))
    val member: Member,
)
