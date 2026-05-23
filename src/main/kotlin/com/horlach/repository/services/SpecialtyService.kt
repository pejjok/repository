package com.horlach.repository.services

import com.horlach.repository.domain.dtos.SpecialtyCreateRequest
import com.horlach.repository.domain.dtos.SpecialtyResponse
import com.horlach.repository.domain.dtos.SpecialtyUpdateRequest
import java.util.UUID

interface SpecialtyService {
    fun createSpecialty(specialtyCreateRequest: SpecialtyCreateRequest): SpecialtyResponse
    fun getSpecialtyById(id: UUID): SpecialtyResponse
    fun getAllSpecialties(): List<SpecialtyResponse>
    fun deleteSpecialty(id: UUID)
    fun updateSpecialty(id: UUID, specialtyUpdateRequest: SpecialtyUpdateRequest): SpecialtyResponse
}