package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.*

data class Track(
    @JsonProperty("album") val album: Album,
    @JsonProperty("artists") val artists: List<Artist>,
    @JsonProperty("available_markets") val availableMarkets: List<String>,
    @JsonProperty("disc_number") val discNumber: Int,
    @JsonProperty("duration_ms") val durationMs: Int,
    @JsonProperty("explicit") val explicit: Boolean,
    @JsonProperty("external_ids") val externalIds: ExternalIds,
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("href") val href: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("is_playable") val isPlayable: Boolean,
    @JsonProperty("linked_from") val linkedFrom: Any?,
    @JsonProperty("restrictions") val restrictions: Restrictions?,
    @JsonProperty("name") val name: String,
    @JsonProperty("popularity") val popularity: Int,
    @JsonProperty("preview_url") val previewUrl: String?,
    @JsonProperty("track_number") val trackNumber: Int,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String,
    @JsonProperty("is_local") val isLocal: Boolean
)
