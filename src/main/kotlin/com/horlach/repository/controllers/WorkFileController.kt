package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.FileReqResponse
import com.horlach.repository.domain.dtos.FileReqUpdateRequest
import com.horlach.repository.domain.dtos.WorkFileResponse
import com.horlach.repository.security.UserDetailsImpl
import com.horlach.repository.services.WorkFileRequestService
import com.horlach.repository.services.WorkFileService
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/v1/work-files")
class WorkFileController(
    private val workFileService: WorkFileService,
    private val workFileRequestService: WorkFileRequestService
) {
    @PostMapping
    fun uploadFile(
        @RequestBody file: MultipartFile
    ): ResponseEntity<WorkFileResponse>{
        return ResponseEntity.status(HttpStatus.CREATED).body(workFileService.saveWorkFile(file))
    }

    @GetMapping("/{id}")
    fun getFile(
        @PathVariable id: UUID
    ): ResponseEntity<WorkFileResponse>{
        return ResponseEntity.ok(workFileService.getWorkFile(id))
    }

    @GetMapping("/{id}/download")
    fun downloadFile(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<Resource>{
        val file: Resource = workFileService.getWorkFileAsResource(id, user.getUser())

        return ResponseEntity.ok()
            .contentType(
                MediaTypeFactory
                    .getMediaType(file)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM)
            )
            .body(file)
    }

    @DeleteMapping("/{id}")
    fun deleteFile(
        @PathVariable id: UUID
    ): ResponseEntity<Unit>{
        workFileService.deleteWorkFile(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/request")
    fun getRequest(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<List<FileReqResponse>>{
        return ResponseEntity.status(HttpStatus.CREATED).body(workFileRequestService.getRequests(id, user.getUser()))
    }

    @PostMapping("/{id}/request")
    fun createRequest(
        @PathVariable id: UUID, // work file id
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<FileReqResponse>{
        return ResponseEntity.status(HttpStatus.CREATED).body(workFileRequestService.createRequest(id, user.getUser()))
    }

    @PutMapping("/requests/{id}")
    fun updateRequest(
        @PathVariable id: UUID, // request id
        @Valid @RequestBody request: FileReqUpdateRequest
    ): ResponseEntity<FileReqResponse>{
        return ResponseEntity.ok(workFileRequestService.updateRequest(id, request))
    }

    @GetMapping("/requests")
    fun updateRequest(
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<PagedModel<FileReqResponse>>{
        return ResponseEntity.ok(workFileRequestService.getAllRequests(pageable))
    }
}