package ru.vlados.fileloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FileLoaderApplication

fun main(args: Array<String>) {
    runApplication<FileLoaderApplication>(*args)
}
