package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface GroupRepository: JpaRepository<Group, UUID> {
    fun existsByName(name: String): Boolean
    @Query("select (count(g) > 0) from Group g inner join g.works works where works.group.id = ?1")
    fun existsByWorks_Group_Id(id: UUID): Boolean
}