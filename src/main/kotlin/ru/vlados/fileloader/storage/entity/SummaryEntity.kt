package ru.vlados.fileloader.storage.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

const val SUMMARY_TABLE_NAME = "summary"

@Table(SUMMARY_TABLE_NAME)
data class SummaryEntity(
    @Id
    val id: Long? = null,

    @Column("files_count")
    val filesCount: Int,

    @Column("files_size")
    val filesSize: Long
)
