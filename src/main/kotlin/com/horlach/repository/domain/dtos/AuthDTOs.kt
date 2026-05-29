package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID
import java.time.Instant

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: UserRole,
    val specialtyIds: List<UUID>
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String
)

fun RegisterRequest.toEntity(passwordEncoder: PasswordEncoder, specialties: List<Specialty> = mutableListOf()) = User(
    id = null,
    email = this.email,
    passwordHash = passwordEncoder.encode(this.password)!!,
    role = this.role,
    fullName = this.fullName,
    specialties = specialties,
    createdAt = Instant.now()
)