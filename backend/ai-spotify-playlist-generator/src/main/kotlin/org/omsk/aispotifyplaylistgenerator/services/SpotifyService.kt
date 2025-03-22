package org.omsk.aispotifyplaylistgenerator.services

import org.omsk.aispotifyplaylistgenerator.clients.SpotifyApiClient
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.CreatePlaylistRequest
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.CreatePlaylistResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SpotifyService(
    private val spotifyApiClient: SpotifyApiClient
) {
    fun createPlaylist(playlistRequest: CreatePlaylistRequest, accessToken: String): Mono<ApiResponse<CreatePlaylistResponse>> {
        val user = spotifyApiClient.getUserProfile(accessToken)

        return user.flatMap {
            spotifyApiClient.createPlaylist(playlistRequest, it.id, accessToken)
        }
    }
}