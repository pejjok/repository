package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.RefreshToken
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID
import java.time.Instant

data class RegisterRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:Size(min = 8, message = "Password must be at least {min} characters long")
    val password: String,
    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 10, max = 100, message = "Full name must be between {min} and {max} characters long")
    val fullName: String
)

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:NotBlank(message = "Password is required")
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