package com.horlach.repository.domain

enum class UserRole(val withoutPrefix: String) {
    ROLE_USER("USER"),
    ROLE_SUPERVISOR("SUPERVISOR"),
    ROLE_ADMIN("ADMIN")
}