package com.horlach.repository.controllers

import com.horlach.repository.domain.dtos.ChangeRoleRequest
import com.horlach.repository.domain.dtos.UserResponse
import com.horlach.repository.domain.dtos.UserChangeSpecialtiesRequest
import com.horlach.repository.security.UserDetailsImpl
import com.horlach.repository.services.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
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
    fun findUsers(
        @PageableDefault(page = 0, size = 20) pageable: Pageable
    ): ResponseEntity<PagedModel<UserResponse>> {
        return ResponseEntity.ok(userService.getAllUsers(pageable))
    }

    @GetMapping("/{id}")
    fun findUser(
        @PathVariable id: UUID
    ): ResponseEntity<UserResponse>{
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @PatchMapping
    fun updateUser(
        @NotBlank(message = "Full name cannot be empty")
        @Size(min = 10, max = 100, message = "Full name must be between {min} and {max} characters long")
        @RequestBody
        fullname: String,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<UserResponse>{
        val user = userService.changeName(user.getUser(),fullname)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UserChangeSpecialtiesRequest
    ): ResponseEntity<UserResponse>{
        val user = userService.changeSpecialties(id,request)
        return ResponseEntity.ok(user)
    }

    @PatchMapping("/{id}")
    fun changeRole(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangeRoleRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<UserResponse>{
        val user = userService.changeUserRole(id,request,user.getUser())
        return ResponseEntity.ok(user)
    }
}