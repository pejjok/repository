package com.horlach.repository.domain.dtos

import com.horlach.repository.domain.WorkType
import com.horlach.repository.domain.entity.Group
import com.horlach.repository.domain.entity.ScientificWork
import com.horlach.repository.domain.entity.User
import com.horlach.repository.domain.entity.WorkFile
import java.time.Instant
import java.util.UUID

data class ScientificWorkCreateRequest(
    val title: String,
    val annotation: String,
    val studentFullName: String,
    val groupId: UUID,
    val workType: WorkType,
    val publicationYear: Int,
    val fileId: UUID,
)

data class ScientificWorkUpdateRequest(
    val title: String,
    var annotation: String,
    var studentFullName: String,
    var groupId: UUID,
    var workType: WorkType,
    var publicationYear: Int,
    var fileId: UUID,
    val isArchived: Boolean,
)

data class ScientificWorkIsArchivedRequest(
    val isArchived: Boolean
)

data class ScientificWorkResponse(
    val id: UUID,
    val title: String,
    val annotation: String,
    val studentFullName: String,
    val supervisorId: UUID,
    val groupId: UUID,
    val workType: WorkType,
    val publicationYear: Int,
    val fileId: UUID,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class ScientificWorkShortResponse(
    val id: UUID,
    val title: String,
    val studentFullName: String,
    val supervisorId: UUID,
    val groupId: UUID,
    val workType: WorkType,
    val publicationYear: Int,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

fun ScientificWorkCreateRequest.toEntity(supervisor: User, group: Group, file: WorkFile): ScientificWork {
    val work = ScientificWork(
        id = null,
        title = title,
        annotation = annotation,
        studentFullName = studentFullName,
        supervisor = supervisor,
        group = group,
        workType = workType,
        publicationYear = publicationYear,
        file = file,
        isArchived = false,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    file.work = work //

    return work
}

fun ScientificWork.updateFromRequest(request: ScientificWorkUpdateRequest, group: Group, file: WorkFile): ScientificWork {
    this.title = request.title
    this.annotation = request.annotation
    this.studentFullName = request.studentFullName
    this.group = group
    this.workType = request.workType
    this.publicationYear = request.publicationYear
    this.file = file
    this.isArchived = request.isArchived
    return this
}

fun ScientificWork.updateIsArchived(request: ScientificWorkIsArchivedRequest): ScientificWork {
    this.isArchived = request.isArchived
    return this
}

fun ScientificWork.toResponse(): ScientificWorkResponse = ScientificWorkResponse(
    id = id!!,
    title = title,
    annotation = annotation,
    studentFullName = studentFullName,
    supervisorId = supervisor.id!!,
    groupId = group.id!!,
    workType = workType,
    publicationYear = publicationYear,
    fileId = file.id!!,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ScientificWork.toShortResponse(): ScientificWorkShortResponse = ScientificWorkShortResponse(
    id = id!!,
    title = title,
    studentFullName = studentFullName,
    supervisorId = supervisor.id!!,
    groupId = group.id!!,
    workType = workType,
    publicationYear = publicationYear,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

