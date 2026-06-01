package com.horlach.repository.services

import com.horlach.repository.domain.dtos.FileReqResponse
import com.horlach.repository.domain.dtos.FileReqUpdateRequest
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import java.util.UUID

interface WorkFileRequestService {
    fun createRequest(workFileId: UUID, user: User): FileReqResponse
    fun getRequests(workFileId: UUID, user: User): List<FileReqResponse>
    fun getAllRequests(pageable: Pageable): PagedModel<FileReqResponse>
    fun updateRequest(id: UUID,request: FileReqUpdateRequest): FileReqResponse
    fun isDownloadAllowed(workFile: WorkFile, user: User): Boolean
}
