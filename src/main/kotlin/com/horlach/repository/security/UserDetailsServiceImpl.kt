package com.horlach.repository.security

import com.horlach.repository.domain.entity.User
import com.horlach.repository.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImpl(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username) ?: throw UsernameNotFoundException("User not found with username: $username")
        return UserDetailsImpl(user)
    }
}