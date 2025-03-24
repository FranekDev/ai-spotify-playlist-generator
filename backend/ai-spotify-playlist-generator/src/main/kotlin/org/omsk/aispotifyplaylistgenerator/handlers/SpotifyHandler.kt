package org.omsk.aispotifyplaylistgenerator.handlers

import org.omsk.aispotifyplaylistgenerator.clients.SpotifyApiClient
import org.omsk.aispotifyplaylistgenerator.models.api.ApiError
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.CreatePlaylistRequest
import org.omsk.aispotifyplaylistgenerator.security.JwtUtil
import org.omsk.aispotifyplaylistgenerator.services.SpotifyService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

data class TokenRequest(val code: String)

@Component
class SpotifyHandler(
    private val spotifyService: SpotifyService,
    private val spotifyApiClient: SpotifyApiClient,
    private val jwtUtil: JwtUtil,
    @Qualifier("responseStatusExceptionHandler") private val status: WebExceptionHandler
) {

    fun getAccessToken(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(TokenRequest::class.java)
            .flatMap { tokenRequest ->
                spotifyApiClient.getAccessToken(tokenRequest.code)
                    .flatMap { accessToken ->
                        if (accessToken.error != null) {
                            return@flatMap ServerResponse.badRequest()
                                .bodyValue(ApiResponse<String>(null, accessToken.error))
                        } else {
                            val token = accessToken.data?.let { jwtUtil.generateToken(it.accessToken, 60 * 60) }
                            val cookie = token?.let {
                                ResponseCookie.from("jwt_token", it)
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(60 * 60)
                                    .sameSite("Lax")
                                    .build()
                            }

                            if (cookie != null) {
                                ServerResponse.ok()
                                    .cookie(cookie)
                                    .bodyValue(mapOf("message" to "Successfully authenticated"))
                            }
                            else {
                                ServerResponse.badRequest().bodyValue("Failed to generate token")
                            }
                        }
                    }
            }
            .switchIfEmpty(ServerResponse.badRequest().bodyValue("Invalid request body"))
    }

    fun getUser(request: ServerRequest): Mono<ServerResponse> {
        val spotifyAccessToken = getSpotifyAccessToken(request.headers())

        return spotifyApiClient.getUserProfile(spotifyAccessToken)
            .flatMap { user ->
                ServerResponse.ok().bodyValue(user)
            }
    }

    fun createPlaylist(request: ServerRequest): Mono<ServerResponse> {
        val spotifyAccessToken = getSpotifyAccessToken(request.headers())

        return request.bodyToMono(CreatePlaylistRequest::class.java)
            .flatMap { playlistRequest ->
                spotifyService.createPlaylist(playlistRequest, spotifyAccessToken)
                    .flatMap { createdPlaylist ->
                        if (createdPlaylist.error != null) {
                            return@flatMap ServerResponse.badRequest()
                                .bodyValue(ApiResponse<String>(null, createdPlaylist.error))
                        }

                        val playlistId = createdPlaylist.data?.id
                        if (playlistId == null) {
                            return@flatMap ServerResponse.badRequest()
                                .bodyValue(ApiResponse<Any>(null, ApiError("No playlist ID found", 400)))
                        }

                        spotifyService.getTracks(playlistRequest.tracksAmount, playlistRequest.description)
                            .flatMap { tracksData ->
                                spotifyService.addTracksToPlaylist(playlistId, tracksData, playlistRequest.tracksAmount, spotifyAccessToken)
                                    .flatMap { _ -> ServerResponse.ok().bodyValue(createdPlaylist) }
                            }
                    }
            }
    }

    fun getPlaylist(request: ServerRequest): Mono<ServerResponse> {
        val spotifyAccessToken = getSpotifyAccessToken(request.headers())

        return spotifyApiClient.getPlaylist(request.pathVariable("id"), spotifyAccessToken)
            .flatMap { playlist ->
                ServerResponse.ok().bodyValue(playlist)
            }
    }

    private fun getSpotifyAccessToken(headers: ServerRequest.Headers): String {
        val authHeader = headers.firstHeader("Authorization")
            ?: throw IllegalArgumentException("No Authorization header found")

        if (!authHeader.startsWith("Bearer ")) {
            throw IllegalArgumentException("Invalid Authorization header format")
        }

        val jwtToken = authHeader.substring(7)
        if (!jwtUtil.validateToken(jwtToken)) {
            throw IllegalArgumentException("Invalid token")
        }

        return jwtUtil.getSpotifyAccessToken(jwtToken) ?: throw IllegalArgumentException("No Spotify access token found")
    }
}