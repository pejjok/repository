package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.SpecialtyCreateRequest
import com.horlach.repository.domain.dtos.SpecialtyResponse
import com.horlach.repository.domain.dtos.SpecialtyUpdateRequest
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.services.SpecialtyService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/specialties")
class SpecialtyController(
    private val specialtyService: SpecialtyService
) {

    @PostMapping
    fun createSpecialty(
        @Valid @RequestBody specialtyCreateRequest: SpecialtyCreateRequest
    ): ResponseEntity<SpecialtyResponse>{
        val specialty: SpecialtyResponse = specialtyService.createSpecialty(specialtyCreateRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(specialty)
    }

    @GetMapping("/{id}")
    fun findSpecialty(
        @PathVariable id: UUID
    ): ResponseEntity<SpecialtyResponse>{
        val specialty: SpecialtyResponse = specialtyService.getSpecialtyById(id)
        return ResponseEntity.ok(specialty)
    }

    @GetMapping
    fun findAllSpecialty(): ResponseEntity<List<SpecialtyResponse>> {
        val specialty: List<SpecialtyResponse> = specialtyService.getAllSpecialties()
        return ResponseEntity.ok(specialty)
    }


    @PutMapping("/{id}")
    fun updateSpecialty(
        @Valid @RequestBody specialtyUpdateRequest: SpecialtyUpdateRequest,
        @PathVariable id: UUID
    ): ResponseEntity<SpecialtyResponse> {
        val specialty: SpecialtyResponse = specialtyService.updateSpecialty(id,specialtyUpdateRequest)
        return ResponseEntity.ok(specialty)
    }

    @DeleteMapping("/{id}")
    fun deleteSpecialty(
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        specialtyService.deleteSpecialty(id)
        return ResponseEntity.noContent().build()
    }
}