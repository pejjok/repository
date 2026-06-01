package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class GroupCreateRequest(
    @field:NotBlank(message = "Group name cannot be empty")
    val name: String,
    @field:NotNull(message = "Specialty ID cannot be null")
    val specialtyId: UUID
)

data class GroupResponse(
    val id: UUID,
    val name: String,
    val specialtyId: UUID
)

fun Group.toResponse(): GroupResponse{
    return GroupResponse(
        id = this.id!!,
        name = this.name,
        specialtyId = this.specialty.id!!
    )
}

fun GroupCreateRequest.toEntity(specialty: Specialty): Group{
    return Group(
        id = null,
        name = this.name,
        specialty = specialty
    )
}