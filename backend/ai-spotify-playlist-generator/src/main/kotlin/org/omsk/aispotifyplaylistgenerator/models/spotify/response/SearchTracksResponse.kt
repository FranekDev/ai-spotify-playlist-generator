package org.omsk.aispotifyplaylistgenerator.models.spotify.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.omsk.aispotifyplaylistgenerator.models.spotify.Album
import org.omsk.aispotifyplaylistgenerator.models.spotify.Artist
import org.omsk.aispotifyplaylistgenerator.models.spotify.ExternalIds
import org.omsk.aispotifyplaylistgenerator.models.spotify.Restrictions

data class SpotifySearchTracksResponse(
    val tracks: TracksResponse
)

data class TracksResponse(
    val href: String,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
    val items: List<TrackItem>
)

data class TrackItem(
    val album: Album,
    val artists: List<Artist>,
    @JsonProperty("available_markets")
    val availableMarkets: List<String>?,
    @JsonProperty("disc_number")
    val discNumber: Int,
    @JsonProperty("duration_ms")
    val durationMs: Int,
    val explicit: Boolean,
    @JsonProperty("external_ids")
    val externalIds: ExternalIds,
    @JsonProperty("external_urls")
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    @JsonProperty("is_playable")
    val isPlayable: Boolean,
    @JsonProperty("linked_from")
    val linkedFrom: Any?,
    val restrictions: Restrictions?,
    val name: String,
    val popularity: Int,
    @JsonProperty("preview_url")
    val previewUrl: String?,
    @JsonProperty("track_number")
    val trackNumber: Int,
    val type: String,
    val uri: String,
    @JsonProperty("is_local")
    val isLocal: Boolean
)
