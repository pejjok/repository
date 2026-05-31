package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.ChangeRoleRequest
import com.horlach.repository.domain.dtos.UserResponse
import com.horlach.repository.domain.dtos.UserUpdateRequest
import com.horlach.repository.security.UserDetailsImpl
import com.horlach.repository.services.UserService
import org.hibernate.sql.Update
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun findUsers(): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/{id}")
    fun findUser(
        @PathVariable id: UUID
    ): ResponseEntity<UserResponse>{
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse>{
        val user = userService.updateUser(id,request)
        return ResponseEntity.ok(user)
    }

    @PatchMapping("/{id}")
    fun changeRole(
        @PathVariable id: UUID,
        @RequestBody request: ChangeRoleRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<UserResponse>{
        val user = userService.changeUserRole(id,request,user.getUser())
        return ResponseEntity.ok(user)
    }
}