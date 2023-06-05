package ru.vlados.fileloader.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.vlados.fileloader.service.FileService

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {
    @GetMapping("/start")
    suspend fun startDownloadAndSaveFiles() {
        fileService.downloadAndSaveFiles()
    }

    @GetMapping("/stop")
    suspend fun stopDownloadAndSaveFiles() {
        fileService.stopDownloadAndSaveFiles()
    }
}
