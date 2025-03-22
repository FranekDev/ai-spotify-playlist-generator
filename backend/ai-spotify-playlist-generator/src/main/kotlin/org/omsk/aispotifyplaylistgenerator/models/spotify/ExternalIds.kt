package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalIds(
    @JsonProperty("isrc") val isrc: String?,
    @JsonProperty("ean") val ean: String?,
    @JsonProperty("upc") val upc: String?
)
