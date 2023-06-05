package ru.vlados.fileloader.storage.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.vlados.fileloader.storage.entity.SummaryEntity

interface SummaryRepository : ReactiveCrudRepository<SummaryEntity, Long> {
    @Modifying
    @Query("""
        UPDATE summary
        SET files_count = files_count + :filesCount,
            files_size  = files_size + :filesSize
        FROM file"""
    )
    suspend fun updateSummary(filesCount: Int, filesSize: Int): Flux<Void>
}
