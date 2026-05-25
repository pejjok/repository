package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.WorkFileResponse
import com.horlach.repository.services.WorkFileService
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/v1/work-files")
class WorkFileController(
    private val workFileService: WorkFileService
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
        @PathVariable id: UUID
    ): ResponseEntity<Resource>{
        val file: Resource = workFileService.getWorkFileAsResource(id)

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
}