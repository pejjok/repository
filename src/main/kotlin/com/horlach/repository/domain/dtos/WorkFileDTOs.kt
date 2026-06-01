package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.RequestStatus
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.domain.entity.WorkFileRequest
import jakarta.validation.constraints.NotNull
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
    val title: String,
    val userId: UUID,
    val fullName: String,
    val status: RequestStatus,
    val expiresAt: Instant?,
    val createdAt: Instant
)

data class FileReqUpdateRequest(
    @field:NotNull(message = "Status cannot be null")
    val status: RequestStatus?,
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
    title = workFile.work!!.title,
    userId = user.id!!,
    fullName = user.fullName,
    status = status,
    expiresAt = expiresAt,
    createdAt = createdAt
)

fun WorkFileRequest.updateFromRequest(request: FileReqUpdateRequest): WorkFileRequest{
    this.status = request.status!!
    this.expiresAt = Instant.now().plusSeconds(request.durationInSeconds!!)
    return this
}