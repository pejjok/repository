package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val role: UserRole,
    val groupId: UUID?,
    val specialtyId: UUID?
)

fun User.toResponse() = UserResponse(
    id = this.id!!,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    middleName = this.middleName,
    role = this.role,
    groupId = this.group?.id,
    specialtyId = this.specialty?.id
)

