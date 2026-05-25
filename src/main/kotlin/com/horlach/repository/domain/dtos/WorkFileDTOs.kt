package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.entity.WorkFile
import java.util.UUID
import java.time.Instant

data class WorkFileResponse(
    val id: UUID,
    val fileName: String,
    val originalName: String,
    val fileSize: Long,
    val uploadedAt: Instant
)

fun WorkFile.toResponse() = WorkFileResponse(
    id = id!!,
    fileName = fileName,
    originalName = originalName,
    fileSize = fileSize,
    uploadedAt = uploadedAt
)