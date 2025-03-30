package org.omsk.aispotifyplaylistgenerator.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.omsk.aispotifyplaylistgenerator.ai.clients.AnthropicClient
import org.omsk.aispotifyplaylistgenerator.ai.prompts.SpotifyPrompt
import org.omsk.aispotifyplaylistgenerator.clients.SpotifyApiClient
import org.omsk.aispotifyplaylistgenerator.models.ai.Track
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
    private val anthropicClient: AnthropicClient
) {
    private val BATCH_SIZE: Short = 10

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
        val tracksQuery = tracks.tracks.map { "track:${it.trackName} artist:${it.trackAuthor}" }

        return Flux.fromIterable(tracksQuery)
            .flatMap { trackName ->
                spotifyApiClient.searchTracks(trackName, 10, accessToken)
                    .mapNotNull { searchResponse ->
                        searchResponse.data?.tracks?.items?.firstOrNull()?.id
                    }
                    .filter { it != null }
            }
            .collectList()
            .flatMap { trackIds ->
                val nonNullTrackIds = if (trackIds.filterNotNull().size > tracksAmount) {
                    trackIds.filterNotNull().subList(0, tracksAmount.toInt())
                } else {
                    trackIds.filterNotNull()
                }
                if (nonNullTrackIds.isEmpty()) {
                    return@flatMap Mono.just(ApiResponse(Unit, null))
                }

                spotifyApiClient.addTracksToPlaylist(playlistId, nonNullTrackIds, accessToken)
                    .map { ApiResponse(Unit, null) }
            }
    }

    fun getTracks(tracksAmount: Short, description: String): Mono<Tracks> {
        if (tracksAmount <= BATCH_SIZE) {
            return getTracksWithPrompt(tracksAmount, description, 0, emptyList())
        }

        val fullBatches = tracksAmount / BATCH_SIZE
        val remainder = tracksAmount % BATCH_SIZE

        val batchSizes = mutableListOf<Short>()
        repeat(fullBatches) { batchSizes.add(BATCH_SIZE) }
        if (remainder > 0) {
            batchSizes.add(remainder.toShort())
        }

        return Mono.defer {
            val accumulatedTracks = mutableListOf<Track>()

            Flux.range(0, batchSizes.size)
                .concatMap { index ->
                    val batchSize = batchSizes[index]

                    getTracksWithPrompt(batchSize, description, index, accumulatedTracks)
                        .doOnNext {
                            accumulatedTracks += it.tracks
                        }
                }
                .reduce(Tracks(emptyList())) { acc, next ->
                    Tracks(acc.tracks + next.tracks)
                }
        }
    }


    private fun getTracksWithPrompt(tracksAmount: Short, description: String, batchIndex: Int, generatedTracks: List<Track>): Mono<Tracks> {
        val prompt = SpotifyPrompt()
            .basePrompt()
            .generateTracks(tracksAmount)
            .withDescription(description)
            .apply {
                if (generatedTracks.isNotEmpty()) {
                    differentThan(generatedTracks)
                }
            }
            .build()

        return anthropicClient.prompt(prompt)
            .flatMap { rawResponse ->
                if (rawResponse.error != null) {
                    return@flatMap Mono.error(RuntimeException("Failed to get tracks from AI: ${rawResponse.error.message}"))
                }

                val jsonString = extractJsonFromResponse(rawResponse.data)
                try {
                    val tracks = ObjectMapper().readValue(jsonString, Tracks::class.java)
                    if (tracks.tracks.size > tracksAmount) {
                        val trimmedTracks = Tracks(tracks.tracks.subList(0, tracksAmount.toInt()))
                        return@flatMap Mono.just(trimmedTracks)
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