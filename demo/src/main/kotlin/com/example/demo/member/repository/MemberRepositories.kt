package com.example.demo.member.repository

import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

//JPA/스프링 데이터로 DB 접근,엔티티 영속화/조회
interface MemberRepository : JpaRepository<Member,Long>{
    fun findByLoginId(loginId: String):Member?
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>