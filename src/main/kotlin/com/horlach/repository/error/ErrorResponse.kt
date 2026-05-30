package com.horlach.repository.error

import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val fieldErrors: List<FieldError>? = null
)

data class FieldError(
    val field: String,
    val message: String
)
