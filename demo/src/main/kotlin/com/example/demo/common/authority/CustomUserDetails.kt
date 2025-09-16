package com.example.demo.common.authority

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * JWT 토큰에 포함될 사용자 정보를 관리하는 Custom UserDetails
 * Spring Security의 UserDetails 인터페이스를 구현하여 인증/인가에 사용
 */
data class CustomUserDetails(
    private val memberId: Long,
    private val loginId: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    // 사용자 ID 반환 (JWT에서 추출할 때 사용)
    fun getMemberId(): Long = memberId

    // 사용자 로그인 ID 반환
    override fun getUsername(): String = loginId

    // 비밀번호 (JWT에서는 사용하지 않음)
    override fun getPassword(): String? = null

    // 권한 목록
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    // 계정 만료 여부
    override fun isAccountNonExpired(): Boolean = true

    // 계정 잠금 여부
    override fun isAccountNonLocked(): Boolean = true

    // 자격 증명 만료 여부
    override fun isCredentialsNonExpired(): Boolean = true

    // 계정 활성화 여부
    override fun isEnabled(): Boolean = true

    companion object {
        /**
         * Member 엔티티와 권한 정보로부터 CustomUserDetails 생성
         */
        fun create(memberId: Long, loginId: String, roles: List<String>): CustomUserDetails {
            val authorities = roles.map { role ->
                SimpleGrantedAuthority("ROLE_$role")
            }
            return CustomUserDetails(memberId, loginId, authorities)
        }
    }
}
