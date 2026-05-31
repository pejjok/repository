package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.RefreshToken
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID
import java.time.Instant

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: UUID
)

fun RegisterRequest.toEntity(passwordEncoder: PasswordEncoder) = User(
    id = null,
    email = this.email,
    passwordHash = passwordEncoder.encode(this.password)!!,
    role = UserRole.ROLE_USER,
    fullName = this.fullName,
    specialties = emptyList(),
    createdAt = Instant.now()
)