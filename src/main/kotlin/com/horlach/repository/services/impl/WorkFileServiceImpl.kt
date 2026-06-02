package com.horlach.repository.services.impl

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.WorkFileResponse
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import com.horlach.repository.error.exceptions.InvalidFileTypeException
import com.horlach.repository.error.exceptions.ResourceNotFoundException
import com.horlach.repository.repositories.WorkFileRepository
import com.horlach.repository.services.StorageService
import com.horlach.repository.services.WorkFileRequestService
import com.horlach.repository.services.WorkFileService
import org.springframework.core.io.Resource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.UUID

@Service
class WorkFileServiceImpl(
    private val workFileRepository: WorkFileRepository,
    private val storageService: StorageService,
    private val workFileRequestService: WorkFileRequestService
): WorkFileService {
    override fun saveWorkFile(file: MultipartFile): WorkFileResponse {
        val extension = StringUtils.getFilenameExtension(file.originalFilename)

        if (extension !in listOf("pdf", "docx", "doc")){
            throw InvalidFileTypeException("Invalid file type. Only pdf, docx, doc are allowed.")
        }

        val filename: String = storageService.store(file)
        val finalFilename = "$filename.$extension"
        val originalFilename: String = file.originalFilename ?: finalFilename
        val workFile = WorkFile(
            id = null,
            work = null,
            fileName = finalFilename,
            originalName = originalFilename,
            fileSize = file.size,
            uploadedAt = Instant.now()
        )

        return workFileRepository.save(workFile).toResponse()
    }

    override fun getWorkFile(id: UUID): WorkFileResponse {
        return workFileRepository.findById(id)
            .map { it.toResponse() }
            .orElseThrow { ResourceNotFoundException("Work file with id $id not found") }
    }

    override fun getWorkFileAsResource(id: UUID, user: User): Resource {
        val workFile: WorkFile = workFileRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Work file with id $id not found") }
        if (user.role != UserRole.ROLE_USER || workFileRequestService.isDownloadAllowed(workFile, user)){
            return storageService.loadAsResource(workFile.fileName)
        }
        else{
            throw AccessDeniedException("Download not allowed for user ${user.id} and file $id")
        }
    }

    override fun deleteWorkFile(id: UUID) {
        val workFile: WorkFile = workFileRepository.findById(id).orElse(null) ?: return
        storageService.delete(workFile.fileName)
        workFileRepository.delete(workFile)
    }

    @Scheduled(cron = "0 0 0 */7 * *")
    fun deleteExpiredFiles() {
        val expiredFiles = workFileRepository.findByWorkNullAndUploadedAtLessThan(Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS))
        expiredFiles.forEach {
            storageService.delete(it.fileName)
            workFileRepository.delete(it)
        }
    }
}