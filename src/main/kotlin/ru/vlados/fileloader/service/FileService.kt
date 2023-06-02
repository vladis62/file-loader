package ru.vlados.fileloader.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import ru.vlados.fileloader.service.client.FileClient
import ru.vlados.fileloader.storage.entity.FileEntity
import ru.vlados.fileloader.storage.repository.FileRepository
import ru.vlados.fileloader.storage.repository.SummaryRepository
import ru.vlados.fileloader.utils.getFileExtension
import ru.vlados.fileloader.utils.removePathFromURL
import kotlin.random.Random

const val BATCH_SIZE = 100

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val fileClient: FileClient,
    private val summaryRepository: SummaryRepository
) {

    suspend fun downloadAndSaveFiles() {
        val files = mutableListOf<FileEntity>()
        val parallelism = 10 // Максимальное число одновременных загрузок

        coroutineScope {
            while (true) {
                val urls = generateUrls(parallelism)

                val finalUrls = getFinalUrls(urls)

                val responses = downloadFiles(finalUrls)

                saveFiles(files, finalUrls, responses)

                if (files.size >= BATCH_SIZE) {
                    saveFilesToRepository(files)
                    files.clear()
                    return@coroutineScope
                }
            }
        }
    }

    private suspend fun generateUrls(count: Int): List<String> =
        List(count) {
            "https://loremflickr.com/${Random.nextInt(10, 5000)}/${Random.nextInt(10, 5000)}"
        }

    private suspend fun getFinalUrls(urls: List<String>): List<String> =
        coroutineScope {
            val deferredPaths = urls.map { url ->
                async {
                    requireNotNull(removePathFromURL(url) + fileClient.getFinalPath(url))
                }
            }

            deferredPaths.awaitAll()
        }

    private suspend fun downloadFiles(urls: List<String>): List<ByteArray> =
        coroutineScope {
            val deferredResponses = urls.map { url ->
                async {
                    fileClient.getFile(url)
                }
            }
            deferredResponses.awaitAll()
        }

    private fun saveFiles(files: MutableList<FileEntity>, urls: List<String>, responses: List<ByteArray>) {
        for (i in urls.indices) {
            val url = urls[i]
            val response = responses[i]

            files.add(toFileEntity(url, response))
        }
    }

    private suspend fun saveFilesToRepository(files: List<FileEntity>) {
        fileRepository.saveAll(files).collectList().awaitFirstOrNull()
        summaryRepository.updateSummary().awaitFirstOrNull()
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
