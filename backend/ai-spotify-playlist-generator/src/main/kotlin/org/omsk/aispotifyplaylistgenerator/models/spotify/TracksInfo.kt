package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class TracksInfo(
    @JsonProperty("href") val href: String,
    @JsonProperty("total") val total: Int,
    @JsonProperty("limit") val limit: Int,
    @JsonProperty("offset") val offset: Int,
    @JsonProperty("next") val next: String?,
    @JsonProperty("previous") val previous: String?,
    @JsonProperty("items") val items: List<PlaylistTrack>
)
