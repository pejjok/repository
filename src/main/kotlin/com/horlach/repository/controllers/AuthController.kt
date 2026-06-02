package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.ChangePasswordRequest
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(registerRequest))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(loginRequest))
    }

    @PostMapping("/refresh/{refreshToken}")
    fun refreshToken(
        @PathVariable refreshToken: UUID
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.refreshToken(refreshToken))
    }

    @PostMapping("/change-password")
    fun changePasswordRequest(
        @RequestBody email: String
    ): ResponseEntity<Unit> {
        authService.changePasswordRequest(email)
        return ResponseEntity.ok().build()
    }


    @PostMapping("/change-password/verify")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(
            authService.validatePasswordToken(request)
        )
    }

}
