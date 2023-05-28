package ru.vlados.fileloader.storage

import java.util.UUID

fun generateId() = UUID.randomUUID().toString().replace("-", "")
