package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.WorkFile
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface WorkFileRepository: JpaRepository<WorkFile, UUID> {
    fun findByWorkNullAndUploadedAtLessThan(uploadedAt: Instant): List<WorkFile>
}