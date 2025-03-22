package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Item(
    @JsonProperty("added_at") val addedAt: String,
    @JsonProperty("added_by") val addedBy: AddedBy,
    @JsonProperty("is_local") val isLocal: Boolean,
    @JsonProperty("track") val track: Track
)
