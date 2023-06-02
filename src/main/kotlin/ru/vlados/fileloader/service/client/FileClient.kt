package ru.vlados.fileloader.service.client

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import ru.vlados.fileloader.utils.removePathFromURL

@Component
class FileClient(
    private val webClient: WebClient
) {

    suspend fun getFile(url: String): ByteArray =
         webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .awaitFirst()

    suspend fun getFinalPath(url: String) =
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
