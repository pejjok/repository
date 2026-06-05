package com.horlach.repository.services

import com.horlach.repository.domain.dtos.GroupCreateRequest
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.Specialty
import com.horlach.repository.repositories.GroupRepository
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.services.impl.GroupServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class GroupServiceImplTest {

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var specialtyRepository: SpecialtyRepository

    @InjectMocks
    private lateinit var groupService: GroupServiceImpl

    private lateinit var specialty: Specialty
    private lateinit var group: Group
    private lateinit var groupId: UUID
    private lateinit var specialtyId: UUID

    @BeforeEach
    fun setUp() {
        specialtyId = UUID.randomUUID()
        groupId = UUID.randomUUID()

        specialty = Specialty(
            id = specialtyId,
            name = "Test Specialty",
            code = "123",
            groups = mutableListOf()
        )

        group = Group(
            id = groupId,
            name = "Test Group",
            specialty = specialty,
            works = mutableListOf()
        )
    }

    @Test
    fun `createGroup should successfully create and return group`() {
        val request = GroupCreateRequest(name = "Test Group", specialtyId = specialtyId)

        whenever(groupRepository.existsByName(request.name)).thenReturn(false)
        whenever(specialtyRepository.findById(request.specialtyId)).thenReturn(Optional.of(specialty))
        whenever(groupRepository.save(ArgumentMatchers.any(Group::class.java))).thenReturn(group)

        val result = groupService.createGroup(request)

        assertNotNull(result)
        assertEquals(groupId, result.id)
        assertEquals("Test Group", result.name)
        verify(groupRepository).save(ArgumentMatchers.any(Group::class.java))
    }

    @Test
    fun `createGroup should throw IllegalArgumentException when group name exists`() {
        val request = GroupCreateRequest(name = "Test Group", specialtyId = specialtyId)
        whenever(groupRepository.existsByName(request.name)).thenReturn(true)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            groupService.createGroup(request)
        }
        verify(groupRepository, Mockito.never()).save(ArgumentMatchers.any(Group::class.java))
    }

    @Test
    fun `getGroupById should return group when found`() {
        whenever(groupRepository.findById(groupId)).thenReturn(Optional.of(group))

        val result = groupService.getGroupById(groupId)

        assertNotNull(result)
        assertEquals(groupId, result.id)
        assertEquals("Test Group", result.name)
    }

    @Test
    fun `deleteGroup should delete group when not associated with works`() {
        whenever(groupRepository.findById(groupId)).thenReturn(Optional.of(group))
        whenever(groupRepository.existsByWorks_Group_Id(groupId)).thenReturn(false)

        groupService.deleteGroup(groupId)

        verify(groupRepository).delete(group)
    }
}