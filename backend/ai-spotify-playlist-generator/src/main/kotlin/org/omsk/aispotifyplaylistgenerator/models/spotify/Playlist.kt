package org.omsk.aispotifyplaylistgenerator.models.spotify

import com.fasterxml.jackson.annotation.JsonProperty

data class Playlist(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("public") val isPublic: Boolean,
    @JsonProperty("collaborative") val isCollaborative: Boolean,
    @JsonProperty("owner") val owner: Owner,
    @JsonProperty("images") val images: List<Image>,
    @JsonProperty("tracks") val tracks: TracksInfo,
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("href") val href: String,
    @JsonProperty("uri") val uri: String,
    @JsonProperty("snapshot_id") val snapshotId: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("followers") val followers: Followers?
)
