package org.omsk.aispotifyplaylistgenerator.ai.configs

import io.netty.channel.ChannelOption
import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class AiConfig {
    @Value("\${spring.ai.anthropic.connection-timeout:60000}")
    private val connectionTimeout: Int = 60000

    @Value("\${spring.ai.anthropic.read-timeout:120000}")
    private val readTimeout: Int = 120000

    @Bean
    fun antrophicChatClient(model: AnthropicChatModel): ChatClient {
        return ChatClient.create(model)
    }

    @Bean
    fun anthropicWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .responseTimeout(Duration.ofMillis(readTimeout.toLong()))
            .wiretap(true)

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}