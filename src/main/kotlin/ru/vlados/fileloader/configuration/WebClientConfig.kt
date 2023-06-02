package ru.vlados.fileloader.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient() = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(1048576) // 1 МБ
            }
            .build()
        )
        .build()

}
