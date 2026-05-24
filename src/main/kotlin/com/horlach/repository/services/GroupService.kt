package com.horlach.repository.services

import com.horlach.repository.domain.dtos.GroupCreateRequest
import com.horlach.repository.domain.dtos.GroupResponse
import java.util.UUID

interface GroupService {
    fun createGroup(createRequest: GroupCreateRequest): GroupResponse
    fun getGroupById(id: UUID): GroupResponse
    fun getAllGroups(): List<GroupResponse>
    fun deleteGroup(id: UUID)
}