package com.horlach.repository.services

import com.horlach.repository.domain.dtos.WorkFileResponse
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface WorkFileService {
    fun saveWorkFile(file: MultipartFile): WorkFileResponse
    fun getWorkFile(id: UUID): WorkFileResponse
    fun getWorkFileAsResource(id: UUID): Resource
    fun deleteWorkFile(id: UUID)
}