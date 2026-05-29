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
    private val specialtyRepository: SpecialtyRepository,
    private val passwordEncoder: PasswordEncoder
): UserService {
    override fun createUser(request: RegisterRequest): UserResponse {

        val userEntity: User = if (request.role == UserRole.ROLE_SUPERVISOR){
            val specialties: List<Specialty> = specialtyRepository.findAllById(request.specialtyIds)
            request.toEntity(passwordEncoder, specialties)
        }
        else{
            request.toEntity(passwordEncoder)
        }

        val savedUser = userRepository.save(userEntity)
        return savedUser.toResponse()
    }
}