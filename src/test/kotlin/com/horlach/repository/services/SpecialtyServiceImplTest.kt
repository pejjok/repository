package com.horlach.repository.services

import com.horlach.repository.domain.dtos.SpecialtyCreateRequest
import com.horlach.repository.domain.dtos.SpecialtyUpdateRequest
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.error.exceptions.DeletionConflictException
import com.horlach.repository.error.exceptions.ResourceAlreadyExistsException
import com.horlach.repository.error.exceptions.ResourceNotFoundException
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.services.impl.SpecialtyServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class SpecialtyServiceImplTest {

    @Mock
    private lateinit var specialtyRepository: SpecialtyRepository

    @InjectMocks
    private lateinit var specialtyService: SpecialtyServiceImpl

    private lateinit var specialtyId: UUID
    private lateinit var specialty: Specialty

    @BeforeEach
    fun setUp() {
        specialtyId = UUID.randomUUID()
        specialty = Specialty(
            id = specialtyId,
            name = "Computer Science",
            code = "CS-101",
            groups = mutableListOf(),
            supervisors = mutableListOf()
        )
    }

    @Test
    fun `createSpecialty should successfully create and return response`() {
        val request = SpecialtyCreateRequest(name = "Computer Science", code = "CS-101")

        whenever(specialtyRepository.existsByCode(request.code)).thenReturn(false)
        whenever(specialtyRepository.save(any(Specialty::class.java))).thenReturn(specialty)

        val result = specialtyService.createSpecialty(request)

        assertNotNull(result)
        assertEquals(specialtyId, result.id)
        assertEquals("CS-101", result.code)
        verify(specialtyRepository).save(any(Specialty::class.java))
    }

    @Test
    fun `createSpecialty should throw ResourceAlreadyExistsException when code exists`() {
        val request = SpecialtyCreateRequest(name = "Computer Science", code = "CS-101")
        whenever(specialtyRepository.existsByCode(request.code)).thenReturn(true)

        Assertions.assertThrows(ResourceAlreadyExistsException::class.java) {
            specialtyService.createSpecialty(request)
        }

        verify(specialtyRepository, never()).save(any(Specialty::class.java))
    }

    @Test
    fun `getSpecialtyById should return specialty response when found`() {
        whenever(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty))

        val result = specialtyService.getSpecialtyById(specialtyId)

        assertNotNull(result)
        assertEquals(specialtyId, result.id)
        assertEquals("CS-101", result.code)
    }

    @Test
    fun `getSpecialtyById should throw ResourceNotFoundException when not found`() {
        whenever(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty())

        Assertions.assertThrows(ResourceNotFoundException::class.java) {
            specialtyService.getSpecialtyById(specialtyId)
        }
    }

    @Test
    fun `getAllSpecialties should return list of specialties`() {
        whenever(specialtyRepository.findAll()).thenReturn(listOf(specialty))

        val result = specialtyService.getAllSpecialties()

        assertEquals(1, result.size)
        assertEquals("CS-101", result[0].code)
    }

    @Test
    fun `deleteSpecialty should delete successfully when no conflicts`() {
        whenever(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty))

        specialtyService.deleteSpecialty(specialtyId)

        verify(specialtyRepository).delete(specialty)
    }

}
