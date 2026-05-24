package com.horlach.repository.services

import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.domain.dtos.UserResponse

interface UserService {
    fun createUser(request: RegisterRequest): UserResponse
}