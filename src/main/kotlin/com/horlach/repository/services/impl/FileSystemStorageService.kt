package com.horlach.repository.services.impl

import com.horlach.repository.services.StorageService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileSystemStorageService : StorageService {

    @Value("\${app.storage.location:uploads}")
    private lateinit var storageLocation: String

    private lateinit var rootLocation: Path

    @PostConstruct
    fun init() {
        rootLocation = Paths.get(storageLocation)
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw RuntimeException("Could not initialize storage location", e)
        }
    }

    override fun store(
        file: MultipartFile,
        filename: String
    ): String {

        if (file.isEmpty) {
            throw RuntimeException("Cannot store an empty file")
        }


        val destinationFile = rootLocation
            .resolve(Paths.get(filename))
            .normalize()
            .toAbsolutePath()
        if (destinationFile.parent != rootLocation.toAbsolutePath()) {
            throw RuntimeException("Cannot store file outside of specified location")
        }

        try {
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }
            return filename
        } catch (e: IOException) {
            throw RuntimeException("Failed to store file", e)
        }
    }

    override fun loadAsResource(filename: String): Resource? {
        try {
            val file = rootLocation.resolve(filename)

            val resource: Resource = UrlResource(file.toUri())

            if (resource.exists() || resource.isReadable) {
                return resource
            } else {
                return null
            }
        } catch (e: MalformedURLException) {
            return null
        }
    }

    override fun delete(filename: String) {
        try {
            val file = rootLocation.resolve(filename)
            Files.deleteIfExists(file)
        } catch (e: IOException) {
            throw RuntimeException("Could not delete file: $filename", e)
        }
    }
}