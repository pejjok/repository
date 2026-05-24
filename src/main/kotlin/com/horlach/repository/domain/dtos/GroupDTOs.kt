package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import java.util.UUID

data class GroupCreateRequest(
    val name: String,
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