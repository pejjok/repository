package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.entity.Specialty
import java.util.UUID

data class SpecialtyCreateRequest (
    val code: String,
    val name: String
)

data class SpecialtyUpdateRequest (
    val code: String,
    val name: String
)

data class SpecialtyResponse(
    val id: UUID,
    val code: String,
    val name: String
)

fun SpecialtyCreateRequest.toEntity() = Specialty(
    id = null,
    code = this.code,
    name = this.name
)

fun Specialty.toResponse() = SpecialtyResponse(
    id = this.id!!,
    code = this.code,
    name = this.name
)

fun Specialty.updateFromRequest(request: SpecialtyUpdateRequest): Specialty {
    this.code = request.code
    this.name = request.name
    return this
}