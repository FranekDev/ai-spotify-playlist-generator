package org.omsk.aispotifyplaylistgenerator.ai.clients

import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class AntrophicClient(private val chatClient: ChatClient) {
    fun prompt(message: String): Mono<String?> {
        return Mono.fromCallable {
            chatClient.prompt()
            .user(message)
            .call()
            .content() }
            .subscribeOn(Schedulers.boundedElastic())
    }
}