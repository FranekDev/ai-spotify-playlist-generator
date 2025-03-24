package org.omsk.aispotifyplaylistgenerator.clients

import org.omsk.aispotifyplaylistgenerator.clients.endpoints.SpotifyEndpoint
import org.omsk.aispotifyplaylistgenerator.configs.SpotifyWebClientConfig
import org.omsk.aispotifyplaylistgenerator.models.api.ApiError
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.Playlist
import org.omsk.aispotifyplaylistgenerator.models.spotify.SpotifyApiError
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.CreatePlaylistRequest
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.*

@Component
class SpotifyApiClient(
    private val spotifyWebClientConfig: SpotifyWebClientConfig,
    @Value("\${spring.security.oauth2.client.registration.spotify.clientId}") private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.spotify.clientSecret}") private val clientSecret: String,
    @Value("\${spring.security.oauth2.client.registration.spotify.redirectUri}") private val redirectUri: String
    ) {
    private val spotifyClient = spotifyWebClientConfig.spotifyClient()
    private val spotifyAuthClient = spotifyWebClientConfig.spotifyAuthClient()

    fun getAccessToken(code: String): Mono<ApiResponse<AccessToken>> {
        val url = "api/token"
        val authHeader = "Basic " + Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

        return spotifyAuthClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .body(
                BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("code", code)
                    .with("redirect_uri", redirectUri)
                    .with("client_id", clientId)
                    .with("client_secret", clientSecret)
            )
            .exchangeToMono {
                handleSpotifyResponse(it, AccessToken::class.java)
            }
            .onErrorResume {
                handleSpotifyError<AccessToken>(it, "Error fetching access token")
            }
    }

    fun getUserProfile(accessToken: String): Mono<ApiResponse<SpotifyUserDetail>> {
        return spotifyClient.get()
            .uri(SpotifyEndpoint.ME)
            .header("Authorization", "Bearer $accessToken")
            .exchangeToMono {
                handleSpotifyResponse(it, SpotifyUserDetail::class.java)
            }
            .onErrorResume {
                handleSpotifyError<SpotifyUserDetail>(it, "Error fetching user profile")
            }
    }

    fun createPlaylist(playlist: CreatePlaylistRequest, userId: String, accessToken: String): Mono<ApiResponse<CreatePlaylistResponse>> {
        val uri = SpotifyEndpoint.USER_PLAYLISTS.replace(SpotifyEndpoint.USER_ID_PLACEHOLDER, userId)

        return spotifyClient.post()
            .uri(uri)
            .headers {
                it.contentType = MediaType.APPLICATION_JSON
                it.setBearerAuth(accessToken)
            }
            .body(BodyInserters.fromValue(mapOf(
                "name" to playlist.name,
                "public" to playlist.public
            )))
            .exchangeToMono {
                handleSpotifyResponse(it, CreatePlaylistResponse::class.java)
            }
            .onErrorResume {
                handleSpotifyError<CreatePlaylistResponse>(it, "Error creating playlist")
            }
    }

    fun searchTracks(query: String, tracksAmount: Short, accessToken: String): Mono<ApiResponse<SpotifySearchTracksResponse>> {
        val q = when {
            query.length <= 250 -> query
            else -> {
                val lastCommaIndex = query.substring(0, 250).lastIndexOf(",")
                if (lastCommaIndex > 0) query.substring(0, lastCommaIndex) else query.substring(0, 250)
            }
        }

        return spotifyClient.get()
            .uri {
                it.path("search")
                    .queryParam("q", q)
                    .queryParam("type", "track")
                    .queryParam("limit", tracksAmount)
                    .build()
            }
            .headers { it.setBearerAuth(accessToken) }
            .exchangeToMono {
                handleSpotifyResponse(it, SpotifySearchTracksResponse::class.java)
            }
            .onErrorResume {
                handleSpotifyError<SpotifySearchTracksResponse>(it, "Error searching tracks")
            }
    }

    fun addTracksToPlaylist(playlistId: String, trackIds: List<String>, accessToken: String): Mono<String> {
        val spotifyTracksUris = trackIds.map { "spotify:track:$it" }
        val uri = SpotifyEndpoint.PLAYLIST_TRACKS.replace(SpotifyEndpoint.PLAYLIST_ID_PLACEHOLDER, playlistId)
        return spotifyClient.post()
            .uri(uri)
            .headers {
                it.contentType = MediaType.APPLICATION_JSON
                it.setBearerAuth(accessToken)
            }
            .body(BodyInserters.fromValue(mapOf(
                "uris" to spotifyTracksUris,
                "position" to 0
            )))
            .retrieve()
            .bodyToMono(String::class.java)
    }

    fun getPlaylist(playlistId: String, accessToken: String): Mono<ApiResponse<Playlist>> {
        val uri = SpotifyEndpoint.PLAYLIST.replace(SpotifyEndpoint.PLAYLIST_ID_PLACEHOLDER, playlistId)
        return spotifyClient.get()
            .uri(uri)
            .headers { it.setBearerAuth(accessToken) }
            .exchangeToMono {
                handleSpotifyResponse(it, Playlist::class.java)
            }
            .onErrorResume { e ->
                handleSpotifyError<Playlist>(e, "Error fetching playlist")
            }
    }

    private fun <T> handleSpotifyResponse(response: ClientResponse, responseType: Class<T>): Mono<ApiResponse<T>> {
        return if (response.statusCode().is2xxSuccessful) {
            response.bodyToMono(responseType)
                .map { ApiResponse(it, null) }
        } else {
            response.bodyToMono(SpotifyApiError::class.java)
                .map { spotifyError ->
                    ApiResponse(
                        null,
                        ApiError("Spotify error: ${spotifyError.error.message}", spotifyError.error.status)
                    )
                }
        }
    }

    private fun <T> handleSpotifyError(e: Throwable, message: String): Mono<ApiResponse<T>> {
        val errorMessage = when (e) {
            is WebClientResponseException -> "Spotify API error: ${e.responseBodyAsString} (Status: ${e.statusCode.value()})"
            else -> "$message: ${e.message}"
        }
        return Mono.just(ApiResponse<T>(null, ApiError(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value())))
    }
}