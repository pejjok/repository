package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.entity.Specialty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class SpecialtyCreateRequest (
    @field:NotBlank(message = "Code is required")
    @field:Size(max = 5, message = "Code must be at most {max} characters")
    val code: String,
    @field:NotBlank(message = "Name is required")
    val name: String
)

data class SpecialtyUpdateRequest (
    @field:NotBlank(message = "Code is required")
    @field:Size(max = 5, message = "Code must be at most {max} characters")
    val code: String,
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 10, max = 100, message = "Name must be between {min} and {max} characters")
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