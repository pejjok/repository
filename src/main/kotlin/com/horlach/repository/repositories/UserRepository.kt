package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String?): User?
    @Query("SELECT u.id FROM User u")
    fun findIds(pageable: Pageable): Page<UUID>

    @EntityGraph(attributePaths = ["specialties"])
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    fun findAllByIdsWithSpecialties(ids: List<UUID>): List<User>

    @Query(
        """select (count(u) > 0) from User u inner join u.assignedWorks assignedWorks
where assignedWorks.supervisor.id = ?1"""
    )
    fun existsByAssignedWorks_Supervisor_Id(id: UUID): Boolean
    fun existsByEmail(email: Any): Boolean
}
