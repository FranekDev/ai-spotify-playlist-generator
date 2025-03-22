package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Tracks(
    @JsonProperty("href") val href: String,
    @JsonProperty("limit") val limit: Int,
    @JsonProperty("next") val next: String?,
    @JsonProperty("offset") val offset: Int,
    @JsonProperty("previous") val previous: String?,
    @JsonProperty("total") val total: Int,
    @JsonProperty("items") val items: List<Item>
)
