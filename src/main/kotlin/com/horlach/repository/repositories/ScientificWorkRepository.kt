package com.horlach.repository.repositories

import com.horlach.repository.domain.WorkType
import com.horlach.repository.domain.entity.ScientificWork
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ScientificWorkRepository: JpaRepository<ScientificWork, UUID> {
    @Query(
        value = """
        SELECT w FROM ScientificWork w 
        LEFT JOIN w.group g 
        LEFT JOIN g.specialty s
        WHERE (:title = '' OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:year IS NULL OR w.publicationYear = :year)
          AND (:groupId IS NULL OR g.id = :groupId)
          AND (:specialtyId IS NULL OR s.id = :specialtyId)
          AND (w.workType = :workType OR CAST(:workType AS string) IS NULL)
          AND (:isArchived = w.isArchived)
    """,
        countQuery = """
        SELECT COUNT(w) FROM ScientificWork w 
        LEFT JOIN w.group g 
        LEFT JOIN g.specialty s
        WHERE (:title = '' OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:year IS NULL OR w.publicationYear = :year)
          AND (:groupId IS NULL OR g.id = :groupId)
          AND (:specialtyId IS NULL OR s.id = :specialtyId)
          AND (w.workType = :workType OR CAST(:workType AS string) IS NULL)
          AND (:isArchived = w.isArchived)
    """
    )
    fun findAllByIsArchived(
        pageable: Pageable,
        title: String,
        groupId: UUID?,
        specialtyId: UUID?,
        workType: WorkType?,
        year: Int?,
        isArchived: Boolean
    ): Page<ScientificWork>
}