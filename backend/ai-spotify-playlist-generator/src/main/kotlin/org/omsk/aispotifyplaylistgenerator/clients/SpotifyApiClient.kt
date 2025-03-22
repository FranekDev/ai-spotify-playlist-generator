package org.omsk.aispotifyplaylistgenerator.clients

import org.omsk.aispotifyplaylistgenerator.configs.SpotifyWebClientConfig
import org.omsk.aispotifyplaylistgenerator.models.api.ApiError
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.AccessTokenRequest
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.CreatePlaylistRequest
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.AccessToken
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.CreatePlaylistResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.SpotifyUserDetail
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
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

    fun getAccessToken(code: String): Mono<AccessToken> {
        val body = AccessTokenRequest(
            code = code,
            redirect_uri = redirectUri,
        )

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
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                response.bodyToMono(String::class.java).flatMap { errorBody ->
                    Mono.error(RuntimeException("Spotify API Error: $errorBody"))
                }
            }
            .bodyToMono(AccessToken::class.java)
    }

    fun createPlaylist(accessToken: String, userId: String, playlistName: String): Mono<String> {
        return spotifyClient.post()
            .uri("users/$userId/playlists")
            .header("Authorization", "Bearer $accessToken")
            .body(Mono.just(mapOf("name" to playlistName)), Map::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
    }

    fun getTracks(accessToken: String, trackIds: List<String>): Flux<String> {
        return spotifyClient.get()
            .uri {
                it.path("tracks")
                    .queryParam("ids", trackIds.joinToString(","))
                    .build()
            }
            .retrieve()
            .bodyToFlux(String::class.java)
    }

    fun getUserProfile(accessToken: String): Mono<SpotifyUserDetail> {
        return spotifyClient.get()
            .uri("me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                response.bodyToMono(String::class.java).flatMap { body ->
                    Mono.error(RuntimeException("Failed to fetch user profile: $body"))
                }
            }
            .onStatus({ status -> status == HttpStatus.UNSUPPORTED_MEDIA_TYPE }) { response ->
                response.bodyToMono(String::class.java).flatMap { body ->
                    Mono.error(RuntimeException("Unsupported media type: $body"))
                }
            }
            .bodyToMono(SpotifyUserDetail::class.java)
            .onErrorResume(WebClientResponseException::class.java) {
                if (it.statusCode.is4xxClientError || it.statusCode.is5xxServerError) {
                    Mono.error(RuntimeException("Failed to fetch user profile: ${it.responseBodyAsString}"))
                } else {
                    Mono.error(it)
                }
            }
    }

    fun createPlaylist(playlist: CreatePlaylistRequest, userId: String, accessToken: String): Mono<ApiResponse<CreatePlaylistResponse>> {
        return spotifyClient.post()
            .uri("users/$userId/playlists")
            .headers {
                it.contentType = MediaType.APPLICATION_JSON
                it.setBearerAuth(accessToken)
            }
            .body(BodyInserters.fromValue(mapOf(
                "name" to playlist.name,
                "description" to playlist.description,
                "public" to playlist.public
            )))
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful()) {
                    response.bodyToMono(CreatePlaylistResponse::class.java)
                        .map { ApiResponse(it, null) }
                } else {
                    response.bodyToMono(String::class.java)
                        .map { errorBody ->
                            ApiResponse<CreatePlaylistResponse>(
                                null,
                                ApiError("Spotify error: $errorBody", response.statusCode().value())
                            )
                        }
                }
            }
            .onErrorResume { e ->
                val errorMessage = e.message ?: "Unknown error"
                Mono.just(ApiResponse(
                    null,
                    ApiError(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value())
                ))
            }
    }
}