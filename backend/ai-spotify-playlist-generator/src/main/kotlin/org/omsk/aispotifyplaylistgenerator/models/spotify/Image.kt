package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Image(
    @JsonProperty("url") val url: String,
    @JsonProperty("height") val height: Int?,
    @JsonProperty("width") val width: Int?
)
