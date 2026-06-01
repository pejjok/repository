package com.horlach.repository.services

import com.horlach.repository.domain.WorkType
import com.horlach.repository.domain.dtos.ScientificWorkCreateRequest
import com.horlach.repository.domain.dtos.ScientificWorkIsArchivedRequest
import com.horlach.repository.domain.dtos.ScientificWorkResponse
import com.horlach.repository.domain.dtos.ScientificWorkShortResponse
import com.horlach.repository.domain.dtos.ScientificWorkUpdateRequest
import com.horlach.repository.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import java.util.UUID

interface ScientificWorkService {
    fun createWork(request: ScientificWorkCreateRequest, supervisor: User): ScientificWorkResponse
    fun updateWork(id: UUID, request: ScientificWorkUpdateRequest, supervisor: User): ScientificWorkResponse
    fun archiveWork(id: UUID, request: ScientificWorkIsArchivedRequest, user: User): ScientificWorkResponse
    fun getWorkById(id: UUID, user: User): ScientificWorkResponse
    fun getAllWorks(pageable: Pageable, title: String, groupId: UUID?, specialtyId: UUID?, workType: WorkType?, isArchived: Boolean, user: User): PagedModel<ScientificWorkShortResponse>
    fun deleteWork(id: UUID)
}