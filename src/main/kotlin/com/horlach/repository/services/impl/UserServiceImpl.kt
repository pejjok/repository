package com.horlach.repository.services.impl

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.domain.dtos.UserResponse
import com.horlach.repository.domain.dtos.toEntity
import com.horlach.repository.domain.dtos.toResponse
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import com.horlach.repository.repositories.GroupRepository
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.services.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val specialtyRepository: SpecialtyRepository,
    private val passwordEncoder: PasswordEncoder
): UserService {
    override fun createUser(request: RegisterRequest): UserResponse {

        val userEntity: User = when (request.role) {
            UserRole.STUDENT -> {
                val groupId = request.groupId
                    ?: throw IllegalArgumentException("GroupId is required for STUDENT role")

                val group = groupRepository.findById(groupId)
                    .orElseThrow { IllegalArgumentException("Group with id $groupId not found") }

                request.toEntity(passwordEncoder, group = group)
            }

            UserRole.TEACHER -> {
                val specialtyId = request.specialtyId
                    ?: throw IllegalArgumentException("SpecialtyId is required for TEACHER role")

                val specialty = specialtyRepository.findById(specialtyId)
                    .orElseThrow { IllegalArgumentException("Specialty with id $specialtyId not found") }

                request.toEntity(passwordEncoder, specialty = specialty)
            }

            else -> throw IllegalArgumentException("Invalid role ${request.role}")
        }

        val savedUser = userRepository.save(userEntity)
        return savedUser.toResponse()
    }
}