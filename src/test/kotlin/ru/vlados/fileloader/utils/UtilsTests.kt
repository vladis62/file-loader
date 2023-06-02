package ru.vlados.fileloader.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import ru.vlados.fileloader.utils.getFileExtension
import ru.vlados.fileloader.utils.removePathFromURL
import java.net.MalformedURLException

internal class UtilsTests : StringSpec() {

    init {
        "test removePathFromURL" {
            forAll(
                row("https://loremflickr.com/320/240", "https://loremflickr.com"),
                row("https://loremflickr.com", "https://loremflickr.com"),
                row("invalid-url", "Invalid url")
            ) { url, expected ->
                if (url.startsWith("https")) {
                    val result = removePathFromURL(url)
                    result shouldBe expected
                } else {
                    val exception = shouldThrow<IllegalArgumentException> {
                        removePathFromURL(url)
                    }
                    exception shouldHaveMessage expected
                }
            }
        }

        "test getFileExtension" {
            forAll(
                row("https://example.com/image.jpg", "jpg"),
                row("https://example.com/image", ""),
                row("", "no protocol:")
            ) { url, expected ->
                if (url.startsWith("https")) {
                    val result = getFileExtension(url)
                    result shouldBe expected
                } else {
                    val exception = shouldThrow<MalformedURLException> {
                        getFileExtension(url)
                    }
                    exception shouldHaveMessage expected
                }
            }
        }
    }
}
