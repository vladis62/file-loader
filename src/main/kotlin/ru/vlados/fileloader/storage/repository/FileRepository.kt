package ru.vlados.fileloader.storage.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.vlados.fileloader.storage.entity.FileEntity

interface FileRepository : ReactiveCrudRepository<FileEntity, Long>
