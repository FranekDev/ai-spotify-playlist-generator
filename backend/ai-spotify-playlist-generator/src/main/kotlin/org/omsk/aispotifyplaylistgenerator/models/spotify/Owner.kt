package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.ExternalUrls
import org.omsk.aispotifyplaylistgenerator.models.spotify.response.Followers

data class Owner(
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("followers") val followers: Followers?,
    @JsonProperty("href") val href: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String,
    @JsonProperty("display_name") val displayName: String?
)
