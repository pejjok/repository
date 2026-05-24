package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.services.AuthService
import com.horlach.repository.services.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService
): AuthService {

    override fun register(registerRequest: RegisterRequest): AuthResponse {
        userService.createUser(registerRequest)
        return AuthResponse("token")// TODO: add jwt generator
    }

    override fun login(loginRequest: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )
        return AuthResponse("token")// TODO: add jwt generator
    }
}