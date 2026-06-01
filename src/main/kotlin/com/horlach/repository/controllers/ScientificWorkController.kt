package com.horlach.repository.controllers

import com.horlach.repository.domain.WorkType
import com.horlach.repository.domain.dtos.ScientificWorkCreateRequest
import com.horlach.repository.domain.dtos.ScientificWorkIsArchivedRequest
import com.horlach.repository.domain.dtos.ScientificWorkResponse
import com.horlach.repository.domain.dtos.ScientificWorkShortResponse
import com.horlach.repository.domain.dtos.ScientificWorkUpdateRequest
import com.horlach.repository.security.UserDetailsImpl
import com.horlach.repository.services.ScientificWorkService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/works")
class ScientificWorkController(
    private val scientificWorkService: ScientificWorkService
) {

    @PostMapping
    fun createWork(
        @Valid @RequestBody request: ScientificWorkCreateRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<ScientificWorkResponse> {
        val createdWork = scientificWorkService.createWork(request, user.getUser())
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWork)
    }

    @GetMapping()
    fun getAllWorks(
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "") title: String,
        @RequestParam(required = false) groupId: UUID?,
        @RequestParam(required = false) specialtyId: UUID?,
        @RequestParam(required = false) workType: WorkType?,
        @RequestParam(required = false, defaultValue = "false") isArchived: Boolean,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<PagedModel<ScientificWorkShortResponse>> {
        val works = scientificWorkService.getAllWorks(pageable, title, groupId, specialtyId, workType, isArchived, user.getUser())
        return ResponseEntity.ok(works)
    }

    @GetMapping("/{id}")
    fun getWorkById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<ScientificWorkResponse> {
        val work = scientificWorkService.getWorkById(id, user.getUser())
        return ResponseEntity.ok(work)
    }

    @PutMapping("/{id}")
    fun updateWork(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ScientificWorkUpdateRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<ScientificWorkResponse> {
        val work = scientificWorkService.updateWork(id, request, user.getUser())
        return ResponseEntity.ok(work)
    }

    @PatchMapping("/{id}")
    fun updateIsArchivedWork(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ScientificWorkIsArchivedRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<ScientificWorkResponse> {
        val work = scientificWorkService.archiveWork(id, request, user.getUser())
        return ResponseEntity.ok(work)
    }

    @DeleteMapping("/{id}")
    fun deleteWork(
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        scientificWorkService.deleteWork(id)
        return ResponseEntity.noContent().build()
    }
}