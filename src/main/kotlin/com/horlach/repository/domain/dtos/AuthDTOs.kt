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
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val role: UserRole,
    val groupId: UUID?,
    val specialtyId: UUID?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String
)

fun RegisterRequest.toEntity(passwordEncoder: PasswordEncoder, specialty: Specialty? = null, group: Group? = null) = User(
    id = null,
    email = this.email,
    passwordHash = passwordEncoder.encode(this.password)!!,
    role = this.role,
    firstName = this.firstName,
    lastName = this.lastName,
    middleName = this.middleName,
    group = group,
    specialty = specialty,
    createdAt = Instant.now(),
    updatedAt = Instant.now()
)