package com.horlach.repository.services

import com.horlach.repository.domain.dtos.ChangeRoleRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.domain.dtos.UserResponse
import com.horlach.repository.domain.dtos.UserUpdateRequest
import com.horlach.repository.domain.entity.User
import java.util.UUID

interface UserService {
    fun createUser(request: RegisterRequest): UserResponse
    fun getAllUsers(): List<UserResponse>
    fun getUserById(id: UUID): UserResponse
    fun changeUserRole(id: UUID, request: ChangeRoleRequest, admin: User): UserResponse
    fun updateUser(id: UUID, request: UserUpdateRequest): UserResponse
    fun deleteUser(id: UUID)
}