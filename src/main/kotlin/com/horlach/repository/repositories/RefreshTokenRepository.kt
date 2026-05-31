package com.horlach.repository.repositories

import com.horlach.repository.domain.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RefreshTokenRepository: JpaRepository<RefreshToken, UUID> {
}