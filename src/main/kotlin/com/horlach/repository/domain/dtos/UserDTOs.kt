package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
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

data class UserChangeSpecialtiesRequest(
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

fun User.updateSpecialties(specialties: MutableList<Specialty>): User{
    this.specialties = specialties
    return this
}
