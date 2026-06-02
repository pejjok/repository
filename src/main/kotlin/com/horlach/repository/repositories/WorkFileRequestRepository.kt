package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.domain.entity.WorkFileRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface WorkFileRequestRepository: JpaRepository<WorkFileRequest, UUID> {
    fun findByWorkFileAndUser(workFile: WorkFile, user: User): List<WorkFileRequest>

    @Query("SELECT wfr.id FROM WorkFileRequest wfr")
    fun findIds(pageable: Pageable): Page<UUID>

    @EntityGraph(attributePaths = ["workFile","user"])
    @Query("SELECT u FROM WorkFileRequest u WHERE u.id IN :ids")
    fun findAllByIdsWithSpecialties(ids: List<UUID>): List<WorkFileRequest>
}