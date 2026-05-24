package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupRepository: JpaRepository<Group, UUID> {
    fun existsByName(name: String): Boolean
}