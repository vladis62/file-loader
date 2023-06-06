package ru.vlados.fileloader.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirstOrNull
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import ru.vlados.fileloader.service.client.FileClient
import ru.vlados.fileloader.storage.entity.FileEntity
import ru.vlados.fileloader.storage.repository.FileRepository
import ru.vlados.fileloader.storage.repository.SummaryRepository
import ru.vlados.fileloader.utils.getFileExtension
import ru.vlados.fileloader.utils.removePathFromURL
import java.util.concurrent.atomic.*
import kotlin.random.Random

const val BATCH_SIZE = 100
const val MAX_PARALLEL_DOWNLOAD_FILES = 10
const val MIN_SIZE = 10
const val MAX_SIZE = 5000

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val fileClient: FileClient,
    private val summaryRepository: SummaryRepository
) {

    companion object : KLogging()

    private var isRunning = AtomicBoolean(false)

    suspend fun downloadAndSaveFiles() {
        val files = mutableListOf<FileEntity>()
        isRunning.set(true)

        logger.info("Start download and save files...")
        coroutineScope {
            while (isRunning.get()) {
                val urls = generateUrls(MAX_PARALLEL_DOWNLOAD_FILES)

                val finalUrls = getFinalUrls(urls)

                val responses = downloadFiles(finalUrls)

                saveFiles(files, finalUrls, responses)

                if (files.size >= BATCH_SIZE) {
                    saveFilesToRepository(files)
                    files.clear()
                }
            }
        }
    }

    fun stopDownloadAndSaveFiles() {
        logger.info("Stop download and save files...")
        isRunning.set(false)
    }

    private suspend fun generateUrls(count: Int): List<String> =
        List(count) {
            "https://loremflickr.com/${Random.nextInt(MIN_SIZE, MAX_SIZE)}/${Random.nextInt(MIN_SIZE, MAX_SIZE)}"
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
            logger.info("Downloading files...")
            val deferredResponses = urls.map { url ->
                async {
                    logger.info("Downloading file: $url")
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
        logger.info("Saving $BATCH_SIZE files to repository...")
        fileRepository.saveAll(files).collectList().awaitFirstOrNull()
        val filesSize = files.sumOf { it.size }
        logger.info("Update summary repository...")
        summaryRepository.updateSummary(filesSize = filesSize, filesCount = files.size).awaitFirstOrNull()
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
