package com.horlach.repository.services

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest

interface AuthService {
    fun register(registerRequest: RegisterRequest): AuthResponse
    fun login(loginRequest: LoginRequest): AuthResponse
}