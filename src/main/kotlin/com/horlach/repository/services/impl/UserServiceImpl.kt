package com.horlach.repository.services.impl

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.*
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.domain.entity.User
import com.horlach.repository.error.exceptions.DeletionConflictException
import com.horlach.repository.error.exceptions.ResourceNotFoundException
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.services.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

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

    override fun getAllUsers(): List<UserResponse> {
        return userRepository.findAllWithSpecialties().map { it.toResponse() }
    }

    override fun getUserById(id: UUID): UserResponse {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }
            .toResponse()
    }

    override fun changeUserRole(
        id: UUID,
        request: ChangeRoleRequest,
        admin: User
    ): UserResponse {
        val user: User = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        if (admin.id!! == id){
            throw IllegalArgumentException("You cannot change your own role")
        }

        // If the role matches, do nothing
        if (user.role == request.role)
            return user.toResponse()

        // if role changes from supervisor to admin or user , delete specialties because they dont need them
        if (request.role in listOf(UserRole.ROLE_ADMIN, UserRole.ROLE_USER)){
            user.specialties = emptyList()
        }
        user.changeRoleFromRequest(request)

        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    override fun updateUser(
        id: UUID,
        request: UserUpdateRequest
    ): UserResponse {
        val user: User = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }
        val specialties: List<Specialty> = specialtyRepository.findAllById(request.specialtyIds)
        if (user.role != UserRole.ROLE_SUPERVISOR)
            throw IllegalArgumentException("User with id $id is not a supervisor")

        user.updateFromRequest(request, specialties)
        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    override fun deleteUser(id: UUID) {
        val user = userRepository.findById(id).orElse(null) ?: return
        if (userRepository.hasWorks(id)) {
            throw DeletionConflictException("Cannot delete user with id $id because user have assigned works")
        }
        userRepository.delete(user)
    }
}