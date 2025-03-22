package org.omsk.aispotifyplaylistgenerator.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SpotifyWebClientConfig {

    @Bean
    fun spotifyAuthClient(): WebClient {
        return WebClient.builder().baseUrl("https://accounts.spotify.com/").build()
    }

    @Bean
    fun spotifyClient(): WebClient {
        return WebClient.builder().baseUrl("https://api.spotify.com/v1/").build()
    }
}