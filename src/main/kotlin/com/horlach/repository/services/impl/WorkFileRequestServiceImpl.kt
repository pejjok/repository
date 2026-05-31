package com.horlach.repository.services.impl

import com.horlach.repository.domain.RequestStatus
import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.FileReqResponse
import com.horlach.repository.domain.dtos.FileReqUpdateRequest
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.dtos.updateFromRequest
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.domain.entity.WorkFileRequest
import com.horlach.repository.error.exceptions.ResourceNotFoundException
import com.horlach.repository.repositories.WorkFileRepository
import com.horlach.repository.repositories.WorkFileRequestRepository
import com.horlach.repository.services.WorkFileRequestService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID
@Service
class WorkFileRequestServiceImpl(
    private val workFileRequestRepository: WorkFileRequestRepository,
    private val workFileRepository: WorkFileRepository
) : WorkFileRequestService {
    override fun createRequest(
        workFileId: UUID,
        user: User
    ): FileReqResponse {
        if (user.role != UserRole.ROLE_USER)
            throw IllegalArgumentException("User with id ${user.id} dosent need make request for downloading files")

        val workFile = workFileRepository.findById(workFileId)
            .orElseThrow { ResourceNotFoundException("Work file with id $workFileId not found") }

        val workFileRequest = WorkFileRequest(
            id = null,
            workFile = workFile,
            user = user,
            status = RequestStatus.PENDING,
            expiresAt = null,
            createdAt = Instant.now()
        )

        val savedRequest = workFileRequestRepository.save(workFileRequest)
        return savedRequest.toResponse()
    }

    override fun getRequests(workFileId: UUID, user: User): List<FileReqResponse> {
        val workFile = workFileRepository.findById(workFileId)
            .orElseThrow{ ResourceNotFoundException("Work file with id $workFileId not found") }
        return workFileRequestRepository.findByWorkFileAndUser(workFile, user)
            .sortedByDescending { it.createdAt }
            .map { it.toResponse()  }
    }

    override fun updateRequest(
        id: UUID,
        request: FileReqUpdateRequest
    ): FileReqResponse {
        val workFileRequest = workFileRequestRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Work file request with id $id not found") }

        if (workFileRequest.status != RequestStatus.PENDING) {
            throw IllegalStateException("Only pending requests can be updated")
        }

        workFileRequest.updateFromRequest(request)

        return workFileRequestRepository.save(workFileRequest).toResponse()
    }

    override fun isDownloadAllowed(
        workFile: WorkFile,
        user: User
    ): Boolean {
        val request = workFileRequestRepository.findByWorkFileAndUser(workFile, user).maxByOrNull { it.createdAt } ?: return false// take the last request

        return request.status == RequestStatus.APPROVED && request.expiresAt!! > Instant.now()
    }
}