// MemberRole.kt (별도 파일로 분리)
package com.example.demo.member.entity

import com.example.demo.common.status.ROLE
import jakarta.persistence.*

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    val role: ROLE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(name = "fk_member_role_member_id"))
    val member: Member,
)