//import io.kotest.assertions.assertSoftly
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.collections.shouldContain
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.shouldStartWith
//import io.mockk.clearAllMocks
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.coVerifyAll
//import io.mockk.coVerifyOrder
//import io.mockk.mockk
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import org.springframework.http.MediaType
//import reactor.core.publisher.Flux
//import ru.vlados.fileloader.service.FileService
//import ru.vlados.fileloader.service.client.FileClient
//import ru.vlados.fileloader.storage.entity.FileEntity
//import ru.vlados.fileloader.storage.repository.FileRepository
//import ru.vlados.fileloader.utils.removePathFromURL
//import java.io.File
//import kotlin.random.Random
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class FileServiceTest : ShouldSpec({
//
//    val fileRepository: FileRepository = mockk(relaxed = true)
//    val fileClient: FileClient = mockk(relaxed = true)
//
//    val fileService = FileService(fileRepository, fileClient)
//
//    beforeTest {
//        clearAllMocks()
//    }
//
//    context("downloadAndSaveFiles") {
//        should("download and save files") {
//            val urls = listOf("url1", "url2", "url3")
//            val finalPaths = listOf("path1", "path2", "path3")
//            val responses = listOf(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6), byteArrayOf(7, 8, 9))
//
//            coEvery { fileService.generateUrls(any()) } returns urls
//            coEvery { fileService.getFinalPaths(urls) } returns finalPaths
//            coEvery { fileService.downloadFiles(urls, finalPaths) } returns responses
//            coEvery { fileService.saveFiles(any(), urls, responses) } coAnswers { /* do nothing */ }
//            coEvery { fileService.saveFilesToRepository(any()) } coAnswers { /* do nothing */ }
//
//            runBlocking {
//                fileService.downloadAndSaveFiles()
//            }
//
//            assertSoftly {
//                coVerifyOrder {
//                    fileService.generateUrls(10)
//                    fileService.getFinalPaths(urls)
//                    fileService.downloadFiles(urls, finalPaths)
//                    fileService.saveFiles(any(), urls, responses)
//                    fileService.saveFilesToRepository(any())
//                }
//            }
//        }
//    }
//
//    context("generateUrls") {
//        should("return a list of URLs with the specified count") {
//            val count = 5
//            val urls = fileService.generateUrls(count)
//
//            urls.size shouldBe count
//            urls.forEach {
//                it.shouldStartWith("https://loremflickr.com/")
//            }
//        }
//    }
//
//    context("getFinalPaths") {
//        should("return a list of final paths for the given URLs") {
//            val urls = listOf("url1", "url2", "url3")
//            val finalPaths = listOf("path1", "path2", "path3")
//
//            coEvery { fileClient.getFinalPath(any()) } returnsMany finalPaths
//
//            runBlocking {
//                val result = fileService.getFinalPaths(urls)
//
//                result shouldBe finalPaths
//                coVerifyAll {
//                    urls.forEachIndexed { index, url ->
//                        fileClient.getFinalPath(url)
//                    }
//                }
//            }
//        }
//    }
//
//    context("downloadFiles") {
//        should("download files and return a list of responses") {
//            val urls = listOf("url1", "url2", "url3")
//            val finalPaths = listOf("path1", "path2", "path3")
//            val responses = listOf(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6), byteArrayOf(7, 8, 9))
//
//            coEvery { removePathFromURL(any()) } coAnswers { firstArg<String>() }
//            coEvery { fileClient.getFile(any()) } returnsMany responses
//
//            runBlocking {
//                val result = fileService.downloadFiles(urls, finalPaths)
//
//                result shouldBe responses
//                coVerifyAll {
//                    urls.forEachIndexed { index, url ->
//                        val redirectUrl = url + finalPaths[index]
//                        removePathFromURL(url)
//                        fileClient.getFile(redirectUrl)
//                    }
//                }
//            }
//        }
//    }
//
//    context("saveFiles") {
//        should("add files to the list") {
//            val files = mutableListOf<FileEntity>()
//            val urls = listOf("url1", "url2", "url3")
//            val responses = listOf(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6), byteArrayOf(7, 8, 9))
//
//            fileService.saveFiles(files, urls, responses)
//
//            assertSoftly {
//                files.size shouldBe urls.size
//                for (i in urls.indices) {
//                    val url = urls[i]
//                    val response = responses[i]
//                    val expectedEntity = fileService.toFileEntity(url, response)
//                    files shouldContain expectedEntity
//                }
//            }
//        }
//    }
//
////    context("saveFilesToRepository") {
////        should("save files to the repository") {
////            val files = listOf(FileEntity(url = "url1", size = 3, contentType = "content_type1", content = byteArrayOf(1, 2, 3)))
////
////            coEvery { fileRepository.saveAll(any<MutableList<File>>()) } returns Flux.just(files)
////
////            runBlocking {
////                fileService.saveFilesToRepository(files)
////
////                coVerify {
////                    fileRepository.saveAll(files)
////                }
////            }
////        }
////    }
//
//    context("toFileEntity") {
//        should("create a FileEntity with the specified values") {
//            val url = "url"
//            val response = byteArrayOf(1, 2, 3)
//            val expectedEntity = FileEntity(url = url, size = response.size, contentType = "content_type", content = response)
//
//            val result = fileService.toFileEntity(url, response)
//
//            result shouldBe expectedEntity
//        }
//    }
//
//    context("determineContentType") {
//        should("return the correct content type based on the file extension") {
//            val jpegUrl = "file.jpg"
//            val pngUrl = "file.png"
//            val gifUrl = "file.gif"
//            val pdfUrl = "file.pdf"
//            val unknownUrl = "file.txt"
//
//            val jpegContentType = fileService.determineContentType(jpegUrl)
//            val pngContentType = fileService.determineContentType(pngUrl)
//            val gifContentType = fileService.determineContentType(gifUrl)
//            val pdfContentType = fileService.determineContentType(pdfUrl)
//            val unknownContentType = fileService.determineContentType(unknownUrl)
//
//            assertSoftly {
//                jpegContentType shouldBe MediaType.IMAGE_JPEG_VALUE
//                pngContentType shouldBe MediaType.IMAGE_PNG_VALUE
//                gifContentType shouldBe MediaType.IMAGE_GIF_VALUE
//                pdfContentType shouldBe MediaType.APPLICATION_PDF_VALUE
//                unknownContentType shouldBe MediaType.APPLICATION_OCTET_STREAM_VALUE
//            }
//        }
//    }
//})
