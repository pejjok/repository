package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.Specialty
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpecialtyRepository: JpaRepository<Specialty, UUID> {
    fun existsByCode(code: String): Boolean
}