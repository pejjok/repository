package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.ScientificWork
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ScientificWorkRepository: JpaRepository<ScientificWork, UUID> {
    fun findAllByIsArchived(isArchived: Boolean): List<ScientificWork>
}