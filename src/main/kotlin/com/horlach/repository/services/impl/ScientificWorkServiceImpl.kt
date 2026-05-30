package com.horlach.repository.services.impl

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.ScientificWorkCreateRequest
import com.horlach.repository.domain.dtos.ScientificWorkIsArchivedRequest
import com.horlach.repository.domain.dtos.ScientificWorkResponse
import com.horlach.repository.domain.dtos.ScientificWorkShortResponse
import com.horlach.repository.domain.dtos.ScientificWorkUpdateRequest
import com.horlach.repository.domain.dtos.toEntity
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.dtos.toShortResponse
import com.horlach.repository.domain.dtos.updateFromRequest
import com.horlach.repository.domain.dtos.updateIsArchived
import com.horlach.repository.domain.entity.ScientificWork
import com.horlach.repository.domain.entity.User
import com.horlach.repository.repositories.GroupRepository
import com.horlach.repository.repositories.ScientificWorkRepository
import com.horlach.repository.repositories.WorkFileRepository
import com.horlach.repository.services.ScientificWorkService
import com.horlach.repository.services.WorkFileService
import org.springframework.security.access.AccessDeniedException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ScientificWorkServiceImpl(
    private val scientificWorkRepository: ScientificWorkRepository,
    private val groupRepository: GroupRepository,
    private val workFileRepository: WorkFileRepository,
    private val workFileService: WorkFileService
) : ScientificWorkService {
    @Transactional
    override fun createWork(
        request: ScientificWorkCreateRequest,
        supervisor: User
    ): ScientificWorkResponse {
        val group = groupRepository.findById(request.groupId)
            .orElseThrow { EntityNotFoundException("Group with id ${request.groupId} not found") }

        val file = workFileRepository.findById(request.fileId)
            .orElseThrow { EntityNotFoundException("File with id ${request.fileId} not found") }

        if (supervisor.role == UserRole.ROLE_USER) {
            throw AccessDeniedException("Only supervisors or admins can create works")
        }

        if (supervisor.specialties.none { it.id == group.specialty.id } && supervisor.role != UserRole.ROLE_ADMIN) {
            throw IllegalArgumentException("Supervisor does not have the specialty of the group")
        }

        val savedWork: ScientificWork = scientificWorkRepository.save(request.toEntity(supervisor, group, file))

        return savedWork.toResponse()
    }

    @Transactional
    override fun updateWork(
        id: UUID,
        request: ScientificWorkUpdateRequest,
        supervisor: User
    ): ScientificWorkResponse {
        val work = scientificWorkRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Work with id $id not found") }

        val group = groupRepository.findById(request.groupId)
            .orElseThrow { EntityNotFoundException("Group with id ${request.groupId} not found") }

        val file = workFileRepository.findById(request.fileId)
            .orElseThrow { EntityNotFoundException("File with id ${request.fileId} not found") }

        if (supervisor.id != work.supervisor.id) {
            throw AccessDeniedException("Only the supervisor of the work can update it")
        }

        val savedWork = scientificWorkRepository.save(work.updateFromRequest(request, group, file))
        return savedWork.toResponse()
    }

    override fun archiveWork(
        id: UUID,
        request: ScientificWorkIsArchivedRequest,
        user: User
    ): ScientificWorkResponse {
        val work = scientificWorkRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Work with id $id not found") }

        if (user.id != work.supervisor.id) {
            throw AccessDeniedException("Only the supervisor of the work can update it")
        }

        val savedWork = scientificWorkRepository.save(work.updateIsArchived(request))

        return savedWork.toResponse()
    }

    override fun getWorkById(
        id: UUID,
        user: User
    ): ScientificWorkResponse {
        val work = scientificWorkRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Work with id $id not found") }

        if (!work.isArchived) {
            return work.toResponse()
        }

        if (user.role == UserRole.ROLE_USER) {
            throw AccessDeniedException("Only supervisors or admins can get archived works")
        }

        return work.toResponse()
    }

    override fun getAllWorks(
        showArchived: Boolean,
        user: User
    ): List<ScientificWorkShortResponse> {
        if (!showArchived) {
            return scientificWorkRepository.findAllByIsArchived(false)
                .map { it.toShortResponse() }
        }

        if (user.role == UserRole.ROLE_USER) {
            throw AccessDeniedException("Only supervisors or admins can get archived works")
        }

        return scientificWorkRepository.findAll()
            .map { it.toShortResponse() }
    }

    @Transactional
    override fun deleteWork(id: UUID) {
        val work = scientificWorkRepository.findById(id).orElse(null) ?: return
        workFileService.deleteWorkFile(work.file.id!!)
        scientificWorkRepository.deleteById(id)
    }
}