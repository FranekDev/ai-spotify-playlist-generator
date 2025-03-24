package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("id") val id: String,
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("href") val href: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String,
    @JsonProperty("followers") val followers: Followers?
)
