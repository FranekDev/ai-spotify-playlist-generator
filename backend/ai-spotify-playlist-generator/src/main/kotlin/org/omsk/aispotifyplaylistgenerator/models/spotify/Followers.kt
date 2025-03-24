package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Followers(
    @JsonProperty("href") val href: String?,
    @JsonProperty("total") val total: Int
)
