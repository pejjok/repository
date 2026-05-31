package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String?): User?
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.specialties")
    fun findAllWithSpecialties(): List<User>
    @Query("SELECT COUNT(w) > 0 FROM Work w WHERE w.user.id = :userId")
    fun hasWorks(@Param("userId") userId: UUID): Boolean
}