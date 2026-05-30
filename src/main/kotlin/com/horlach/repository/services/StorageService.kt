package com.horlach.repository.services

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile


interface StorageService {
    fun store(file: MultipartFile, filename: String): String
    fun loadAsResource(filename: String): Resource
    fun delete(filename: String)
}