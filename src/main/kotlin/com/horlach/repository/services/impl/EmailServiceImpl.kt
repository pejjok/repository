package com.horlach.repository.services.impl

import com.horlach.repository.services.EmailService
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender
): EmailService {

    override fun send(text: String, email: String) {
        val msg: SimpleMailMessage = SimpleMailMessage()

        msg.setTo(email)
        msg.subject = "Репозиторій"
        msg.text = text

        mailSender.send(msg)
    }
}