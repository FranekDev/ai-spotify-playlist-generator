package org.omsk.aispotifyplaylistgenerator.models.spotify.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.omsk.aispotifyplaylistgenerator.models.spotify.Owner
import org.omsk.aispotifyplaylistgenerator.models.spotify.Tracks

data class CreatePlaylistResponse(
    @JsonProperty("collaborative") val collaborative: Boolean,
    @JsonProperty("description") val description: String,
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("followers") val followers: Followers,
    @JsonProperty("href") val href: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("images") val images: List<Image>,
    @JsonProperty("name") val name: String,
    @JsonProperty("owner") val owner: Owner,
    @JsonProperty("public") val isPublic: Boolean,
    @JsonProperty("snapshot_id") val snapshotId: String,
    @JsonProperty("tracks") val tracks: Tracks,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String
)
