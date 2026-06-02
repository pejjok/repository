package com.horlach.repository.services

interface EmailService {
    fun send(text: String, email: String)
}