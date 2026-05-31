package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.domain.entity.WorkFileRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkFileRequestRepository: JpaRepository<WorkFileRequest, UUID> {
    fun findByWorkFileAndUser(workFile: WorkFile, user: User): List<WorkFileRequest>
}