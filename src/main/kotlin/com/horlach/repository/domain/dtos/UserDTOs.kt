package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val specialtyIds: List<UUID>
)


data class ChangeRoleRequest(
    @field:NotNull(message = "Role cannot be null")
    val role: UserRole?
)

data class UserUpdateRequest(
    @field:NotBlank(message = "Full name cannot be empty")
    val fullName: String?,

    @field:Email(message = "Invalid email format")
    val email: String?,

    val specialtyIds: List<UUID>
)


fun User.toResponse() = UserResponse(
    id = this.id!!,
    email = this.email,
    fullName = this.fullName,
    role = this.role,
    specialtyIds = this.specialties.map { it.id!! }
)

fun User.changeRoleFromRequest(changeRoleRequest: ChangeRoleRequest): User{
    this.role = changeRoleRequest.role!!
    return this
}

fun User.updateFromRequest(userUpdateRequest: UserUpdateRequest, specialties: List<Specialty>): User{
    this.fullName = userUpdateRequest.fullName!!
    this.specialties = specialties
    return this
}
