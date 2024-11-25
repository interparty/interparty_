package com.sparta.interparty.global.security

import com.sparta.interparty.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    private val user: User
) : UserDetails {

    fun getUser(): User? {
        return user
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val userRole = user.userRole
        // Spring Security는 "권한"을 이해하려면 SimpleGrantedAuthority라는 특정한 형태로 전달받아야 한다
        val simpleGrantedAuthority = SimpleGrantedAuthority(userRole.name)
        // 사용자가 여러 개의 역할을 가질 수도 있으니까, 역할을 보관할 수 있는 목록을 만듬
        val authorities: MutableCollection<GrantedAuthority> = ArrayList()
        authorities.add(simpleGrantedAuthority)

        return authorities
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}