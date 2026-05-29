package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.GroupCreateRequest
import com.horlach.repository.domain.dtos.GroupResponse
import com.horlach.repository.domain.dtos.toEntity
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.repositories.GroupRepository
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.services.GroupService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val specialtyRepository: SpecialtyRepository
): GroupService {
    override fun createGroup(createRequest: GroupCreateRequest): GroupResponse {
        if (groupRepository.existsByName(createRequest.name)){
            throw IllegalArgumentException("Group with name ${createRequest.name} already exists")
        }
        val specialty: Specialty = specialtyRepository.findById(createRequest.specialtyId)
            .orElseThrow{ NoSuchElementException("Specialty with id ${createRequest.specialtyId} not found") }

        val group: Group = createRequest.toEntity(specialty)
        val savedGroup: Group = groupRepository.save(group)
        return savedGroup.toResponse()
    }

    override fun getGroupById(id: UUID): GroupResponse {
        return groupRepository.findById(id)
            .orElseThrow{NoSuchElementException("Group with id $id not found")}
            .toResponse()
    }

    override fun getAllGroups(): List<GroupResponse> {
        return groupRepository.findAll()
            .map { it.toResponse() }
    }

    @Transactional
    override fun deleteGroup(id: UUID) {
        val group: Group = groupRepository.findById(id).orElse(null) ?: return

        if (group.works.isNotEmpty())
            throw IllegalStateException("Group with id $id associated with works")

        groupRepository.delete(group)
    }
}