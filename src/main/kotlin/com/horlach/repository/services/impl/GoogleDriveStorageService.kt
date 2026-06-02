package com.horlach.repository.services.impl

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.horlach.repository.services.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Base64

@Service
@Profile("prod")
class GoogleDriveStorageService(
    @Value($$"${google.drive.credentials}")
    var credentialsBase64: String,

    @Value($$"${google.drive.folder_id}")
    var folderId: String
): StorageService {

    var credentialsJson = String(Base64.getDecoder().decode(credentialsBase64))
    var credentials: GoogleCredential = GoogleCredential.fromStream(credentialsJson.byteInputStream())
        .createScoped(listOf(DriveScopes.DRIVE))

    var drive: Drive = Drive.Builder(newTrustedTransport(), JacksonFactory(), credentials)
        .build()

    override fun store(file: MultipartFile): String {
        val fileMetadata = File()
            .setName(file.originalFilename)
            .setMimeType(file.contentType)
            .setParents(listOf(folderId))

        val mediaContent = InputStreamContent(null, file.inputStream)

        val uploadedFile = drive.files().create(fileMetadata, mediaContent)
            .setFields("id, name")
            .execute()


        drive.files().list()
        return uploadedFile.id
    }

    override fun loadAsResource(filename: String): Resource {
        return drive.files().get(filename).executeMediaAsInputStream().use { input ->
            InputStreamResource(input)
        }
    }

    override fun delete(filename: String) {
        return
    }
}