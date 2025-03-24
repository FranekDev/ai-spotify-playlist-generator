package org.omsk.aispotifyplaylistgenerator.models.ai

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Tracks @JsonCreator constructor(
    @JsonProperty("tracks") val tracks: List<Track>
)

data class Track @JsonCreator constructor(
    @JsonProperty("track_name") val trackName: String,
    @JsonProperty("track_author") val trackAuthor: String,
    @JsonProperty("released_at") val releasedAt: String
)
