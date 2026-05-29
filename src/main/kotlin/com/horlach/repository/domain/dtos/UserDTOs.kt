package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val specialtyIds: List<UUID>
)

fun User.toResponse() = UserResponse(
    id = this.id!!,
    email = this.email,
    fullName = this.fullName,
    role = this.role,
    specialtyIds = this.specialties.map { it.id!! }
)

