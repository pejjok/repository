package com.horlach.repository.services

import com.horlach.repository.domain.UserRole
import com.horlach.repository.domain.dtos.*
import com.horlach.repository.domain.entity.User
import com.horlach.repository.error.exceptions.DeletionConflictException
import com.horlach.repository.error.exceptions.ResourceNotFoundException
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.services.impl.UserServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var specialtyRepository: SpecialtyRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    private lateinit var userId: UUID
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        userId = UUID.randomUUID()
        user = User(
            id = userId,
            email = "test@example.com",
            passwordHash = "encodedPassword",
            fullName = "Test User",
            role = UserRole.ROLE_USER,
            specialties = mutableListOf()
        )
    }

    @Test
    fun `getUserById should return user response when found`() {
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        val result = userService.getUserById(userId)

        assertNotNull(result)
        assertEquals(userId, result.id)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `getUserById should throw ResourceNotFoundException when not found`() {
        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())

        assertThrows(ResourceNotFoundException::class.java) {
            userService.getUserById(userId)
        }
    }

    @Test
    fun `changeUserRole should successfully change role`() {
        val adminId = UUID.randomUUID()
        val admin = User(id = adminId, email = "admin@ex.com", passwordHash = "pwd", fullName = "Admin", role = UserRole.ROLE_ADMIN)
        val request = ChangeRoleRequest(role = UserRole.ROLE_SUPERVISOR)

        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(userRepository.save(any(User::class.java))).thenReturn(user)

        val result = userService.changeUserRole(userId, request, admin)

        assertEquals(UserRole.ROLE_SUPERVISOR, result.role)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `changeUserRole should throw IllegalArgumentException when changing own role`() {
        val request = ChangeRoleRequest(role = UserRole.ROLE_SUPERVISOR)

        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        assertThrows(IllegalArgumentException::class.java) {
            userService.changeUserRole(userId, request, user) // Admin == User, so it's their own role
        }
    }

    @Test
    fun `changePassword should successfully change password`() {
        val newPassword = "newPassword"
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword")
        whenever(userRepository.save(any(User::class.java))).thenReturn(user)

        userService.changePassword(userId, newPassword)

        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `deleteUser should delete successfully when no assigned works`() {
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(userRepository.existsByAssignedWorks_Supervisor_Id(userId)).thenReturn(false)

        userService.deleteUser(userId)

        verify(userRepository).delete(user)
    }

    @Test
    fun `deleteUser should throw DeletionConflictException when user has assigned works`() {
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(userRepository.existsByAssignedWorks_Supervisor_Id(userId)).thenReturn(true)

        assertThrows(DeletionConflictException::class.java) {
            userService.deleteUser(userId)
        }
        verify(userRepository, never()).delete(any(User::class.java))
    }
}
