package org.omsk.aispotifyplaylistgenerator.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.omsk.aispotifyplaylistgenerator.ai.clients.AntrophicClient
import org.omsk.aispotifyplaylistgenerator.ai.prompts.SpotifyPrompt
import org.omsk.aispotifyplaylistgenerator.clients.SpotifyApiClient
import org.omsk.aispotifyplaylistgenerator.models.ai.Tracks
import org.omsk.aispotifyplaylistgenerator.models.api.ApiError
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.omsk.aispotifyplaylistgenerator.models.spotify.request.CreatePlaylistRequest
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.CreatePlaylistResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class SpotifyService(
    private val spotifyApiClient: SpotifyApiClient,
    private val antrophicClient: AntrophicClient
) {
    fun createPlaylist(playlistRequest: CreatePlaylistRequest, accessToken: String): Mono<ApiResponse<CreatePlaylistResponse>> {
        return spotifyApiClient.getUserProfile(accessToken)
            .flatMap { response ->
                val userId = response.data?.id
                if (userId != null) {
                    spotifyApiClient.createPlaylist(playlistRequest, userId, accessToken)
                } else {
                    Mono.just(ApiResponse(null, ApiError("User profile data is null", 400)))
                }
            }
    }

    fun addTracksToPlaylist(playlistId: String, tracks: Tracks, tracksAmount: Short, accessToken: String): Mono<ApiResponse<Unit>> {
        val tracksQuery = tracks.tracks.joinToString(",") { "track:${it.trackName} artist:${it.trackAuthor}" }
        val tracksNames = tracksQuery.split(",")

        return Flux.fromIterable(tracksNames)
            .flatMap { trackName ->
                spotifyApiClient.searchTracks(trackName, 1, accessToken)
                    .map { searchResponse ->
                        searchResponse.data?.tracks?.items?.firstOrNull()?.id
                    }
                    .filter { it != null }
            }
            .collectList()
            .flatMap { trackIds ->
                val nonNullTrackIds = trackIds.filterNotNull()
                if (nonNullTrackIds.isEmpty()) {
                    return@flatMap Mono.just(ApiResponse(Unit, null))
                }

                spotifyApiClient.addTracksToPlaylist(playlistId, nonNullTrackIds, accessToken)
                    .map { ApiResponse(Unit, null) }
            }
    }

    fun getTracks(tracksAmount: Short, description: String): Mono<Tracks> {
        val prompt = SpotifyPrompt()
            .basePrompt()
            .generateTracks(tracksAmount)
            .withDescription(description)
            .build()

        return antrophicClient.prompt(prompt)
            .flatMap { rawResponse ->
                val jsonString = extractJsonFromResponse(rawResponse)
                try {
                    val tracks = ObjectMapper().readValue(jsonString, Tracks::class.java)
                    if (tracks.tracks.size > tracksAmount) {
                        tracks.tracks.subList(0, tracksAmount.toInt())
                    }
                    Mono.just(tracks)
                } catch (e: Exception) {
                    Mono.error(RuntimeException("Failed to parse tracks JSON: ${e.message}"))
                }
            }
    }

    private fun extractJsonFromResponse(response: String?): String {
        if (response.isNullOrBlank()) return "{}"

        val startIndex = response.indexOf("{")
        if (startIndex == -1) return "{}"

        val endIndex = response.lastIndexOf("}") + 1
        if (endIndex == 0) return "{}"

        return response.substring(startIndex, endIndex)
    }
}