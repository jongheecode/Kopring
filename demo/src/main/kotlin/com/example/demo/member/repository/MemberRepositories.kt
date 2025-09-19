package com.example.demo.member.repository

import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>{
    fun findByLoginId(loginId: String): Member?
    fun existsByLoginId(loginId: String): Boolean // 추가된 메서드
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>{
    fun findByMember(member: Member): List<MemberRole> // 추가된 메서드
}