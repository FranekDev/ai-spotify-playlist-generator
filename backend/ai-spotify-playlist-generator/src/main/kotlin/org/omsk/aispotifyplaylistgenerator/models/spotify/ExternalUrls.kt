package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalUrls(
    @JsonProperty("spotify") val spotify: String
)
