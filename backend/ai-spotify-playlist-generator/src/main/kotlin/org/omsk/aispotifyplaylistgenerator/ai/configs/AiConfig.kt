package org.omsk.aispotifyplaylistgenerator.ai.configs

import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiConfig {
    @Bean
    fun antrophicChatClient(model: AnthropicChatModel): ChatClient {
        return ChatClient.create(model)
    }
}