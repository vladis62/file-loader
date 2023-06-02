package ru.vlados.fileloader.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.vlados.fileloader.service.FileService

@RestController
class FileController(
    private val fileService: FileService
) {
    @PostMapping("/files")
    suspend fun downloadAndSaveFileAsync(@RequestParam url: String) {
        fileService.downloadAndSaveFile(url)
    }
}
