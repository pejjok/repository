package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.SpecialtyCreateRequest
import com.horlach.repository.domain.dtos.SpecialtyResponse
import com.horlach.repository.domain.dtos.SpecialtyUpdateRequest
import com.horlach.repository.domain.dtos.toEntity
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.services.SpecialtyService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SpecialtyServiceImpl(
    private val specialtyRepository: SpecialtyRepository
): SpecialtyService {
    override fun createSpecialty(specialtyCreateRequest: SpecialtyCreateRequest): SpecialtyResponse {

        if (specialtyRepository.existsByCode(specialtyCreateRequest.code)){
            throw IllegalArgumentException("Specialty with code ${specialtyCreateRequest.code} already exists")
        }

        val specialty: Specialty = specialtyCreateRequest.toEntity()

        val savedSpecialty: Specialty = specialtyRepository.save(specialty)

        return savedSpecialty.toResponse()
    }

    override fun getSpecialtyById(id: UUID): SpecialtyResponse {
        val specialty: Specialty = specialtyRepository.findById(id)
            .orElseThrow { NoSuchElementException("Specialty with id $id not found")  }

        return specialty.toResponse()
    }

    override fun getAllSpecialties(): List<SpecialtyResponse> {
        val specialties: List<Specialty> = specialtyRepository.findAll()

        return specialties.map { it.toResponse() }
    }

    override fun deleteSpecialty(id: UUID) {
        return specialtyRepository.deleteById(id)
    }

    override fun updateSpecialty(
        id: UUID,
        specialtyUpdateRequest: SpecialtyUpdateRequest
    ): SpecialtyResponse {
        val specialty: Specialty = specialtyRepository.findById(id)
            .orElseThrow { NoSuchElementException("Specialty with id $id not found")  }

        specialty.name = specialtyUpdateRequest.name

        val updatedSpecialty: Specialty = specialtyRepository.save(specialty)

        return updatedSpecialty.toResponse()
    }
}