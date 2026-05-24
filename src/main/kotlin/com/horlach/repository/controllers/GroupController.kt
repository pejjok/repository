package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.GroupCreateRequest
import com.horlach.repository.domain.dtos.GroupResponse
import com.horlach.repository.services.GroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/groups")
class GroupController(
    private val groupService: GroupService
) {
    @PostMapping
    fun createGroup(
        @RequestBody createRequest: GroupCreateRequest
    ) : ResponseEntity<GroupResponse> {
        val groupResponse: GroupResponse = groupService.createGroup(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(groupResponse)
    }

    @GetMapping
    fun getAllGroups(): ResponseEntity<List<GroupResponse>> {
        val groups: List<GroupResponse> = groupService.getAllGroups()
        return ResponseEntity.ok(groups)
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        groupService.deleteGroup(id)
        return ResponseEntity.noContent().build()
    }
}
