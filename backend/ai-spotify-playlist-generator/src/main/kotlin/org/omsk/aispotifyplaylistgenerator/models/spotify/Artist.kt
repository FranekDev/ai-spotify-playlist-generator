package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.ExternalUrls

data class Artist(
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("href") val href: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String
)
