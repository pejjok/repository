package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.RequestStatus
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.domain.entity.WorkFileRequest
import java.util.UUID
import java.time.Instant

data class WorkFileResponse(
    val id: UUID,
    val fileName: String,
    val originalName: String,
    val fileSize: Long,
    val uploadedAt: Instant
)

data class FileReqResponse(
    val id: UUID,
    val workFileId: UUID,
    val userId: UUID,
    val status: RequestStatus,
    val expiresAt: Instant?,
    val createdAt: Instant
)

data class FileReqUpdateRequest(
    val status: RequestStatus,
    val durationInSeconds: Long?
)

fun WorkFile.toResponse() = WorkFileResponse(
    id = id!!,
    fileName = fileName,
    originalName = originalName,
    fileSize = fileSize,
    uploadedAt = uploadedAt
)

fun WorkFileRequest.toResponse() = FileReqResponse(
    id = id!!,
    workFileId = workFile.id!!,
    userId = user.id!!,
    status = status,
    expiresAt = expiresAt,
    createdAt = createdAt
)

fun WorkFileRequest.updateFromRequest(request: FileReqUpdateRequest): WorkFileRequest{
    this.status = request.status
    this.expiresAt = Instant.now().plusSeconds(request.durationInSeconds!!)
    return this
}