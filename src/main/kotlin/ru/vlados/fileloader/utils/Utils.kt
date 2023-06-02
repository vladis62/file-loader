package ru.vlados.fileloader.utils

import java.net.URI
import java.net.URL
import java.util.UUID

fun generateId() = UUID.randomUUID().toString().replace("-", "")

fun removePathFromURL(url: String): String {
    val uri = URI(url)
    val uriWithoutPath = URI(uri.scheme, uri.authority, null, null, null)
    require(uriWithoutPath.toString() != "") {
        "Invalid url"
    }
    return uriWithoutPath.toString()
}

fun getFileExtension(url: String): String {
    val file = URL(url).file
    val lastDotIndex = file.lastIndexOf('.')
    return if (lastDotIndex != -1 && lastDotIndex < file.length - 1) {
        file.substring(lastDotIndex + 1).lowercase()
    } else {
        ""
    }
}
