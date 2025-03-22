package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Restrictions(
    @JsonProperty("reason") val reason: String
)
