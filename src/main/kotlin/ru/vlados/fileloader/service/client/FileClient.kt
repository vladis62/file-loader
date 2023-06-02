package ru.vlados.fileloader.service.client

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import ru.vlados.fileloader.utils.removePathFromURL

@Component
class FileClient(
    private val webClient: WebClient
) {

    suspend fun getFile(url: String): ByteArray {
        val redirectUrl = removePathFromURL(url) + requireNotNull(getFinalPath(url))
        return webClient.get()
            .uri(redirectUrl)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .awaitFirstOrDefault(byteArrayOf())
    }

    private suspend fun getFinalPath(url: String) =
        webClient.get()
            .uri(url)
            .retrieve()
            .toEntity(ByteArray::class.java)
            .map { response: ResponseEntity<ByteArray?> ->
                response.headers.location
            }
            .awaitFirst()
            ?.path
}
