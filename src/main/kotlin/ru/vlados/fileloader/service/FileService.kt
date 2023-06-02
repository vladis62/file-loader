package ru.vlados.fileloader.service

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.vlados.fileloader.service.client.FileClient
import ru.vlados.fileloader.storage.entity.FileEntity
import ru.vlados.fileloader.storage.getFileExtension
import ru.vlados.fileloader.storage.removePathFromURL
import ru.vlados.fileloader.storage.repository.FileRepository
import java.net.URI

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val fileClient: FileClient
) {

    suspend fun downloadAndSaveFile(url: String): FileEntity? {
        val response = fileClient.getFile(url)

        val fileEntity = toFileEntity(url, response)
        return fileRepository.save(fileEntity).awaitFirstOrNull()
    }

    private fun toFileEntity(
        url: String,
        response: ByteArray
    ) = FileEntity(url = url, size = response.size, contentType = determineContentType(url), content = response)

    private fun determineContentType(url: String): String =
        when (getFileExtension(url)) {
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE
            "png" -> MediaType.IMAGE_PNG_VALUE
            "gif" -> MediaType.IMAGE_GIF_VALUE
            "pdf" -> MediaType.APPLICATION_PDF_VALUE
            else -> MediaType.APPLICATION_OCTET_STREAM_VALUE
        }
}
