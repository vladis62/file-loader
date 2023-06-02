package ru.vlados.fileloader.storage.entity

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.SequenceGenerator
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

const val FILE_TABLE_NAME = "file"

@Table(FILE_TABLE_NAME)
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id")
    var id: Long? = null,
    @Column("url")
    val url: String,
    @Column("size")
    val size: Int,
    @Column("content_type")
    val contentType: String,
    @Column("content")
    val content: ByteArray
)
