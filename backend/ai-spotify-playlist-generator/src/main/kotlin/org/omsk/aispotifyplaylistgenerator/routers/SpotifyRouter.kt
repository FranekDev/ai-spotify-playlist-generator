package org.omsk.aispotifyplaylistgenerator.routers

import org.omsk.aispotifyplaylistgenerator.handlers.SpotifyHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class SpotifyRouter {

    @Bean
    fun spotify(spotifyHandler: SpotifyHandler) = router {
        POST("/token", spotifyHandler::getAccessToken)
        GET("/user", spotifyHandler::getUser)
        POST("/playlist", spotifyHandler::createPlaylist)
        GET("/playlist/{id}", spotifyHandler::getPlaylist)
    }
}