package com.horlach.repository.services

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.ChangePasswordRequest
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

interface AuthService {
    fun register(registerRequest: RegisterRequest): AuthResponse
    fun login(loginRequest: LoginRequest): AuthResponse
    fun validateAccessToken(token: String): UserDetails
    fun refreshToken(refreshToken: UUID): AuthResponse
    fun changePasswordRequest(email: String)
    fun validatePasswordToken(request: ChangePasswordRequest): AuthResponse
}