package ru.vlados.fileloader.storage.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.vlados.fileloader.storage.entity.FILE_TABLE_NAME
import ru.vlados.fileloader.storage.entity.SUMMARY_TABLE_NAME
import ru.vlados.fileloader.storage.entity.SummaryEntity

interface SummaryRepository : ReactiveCrudRepository<SummaryEntity, Long> {
    @Modifying
    @Query("""
        UPDATE $SUMMARY_TABLE_NAME 
        SET filesCount = (SELECT COUNT(*) FROM $FILE_TABLE_NAME), filesSize = (SELECT SUM(size) 
        FROM $FILE_TABLE_NAME)""")
    suspend fun updateSummary(): Flux<Void>
}