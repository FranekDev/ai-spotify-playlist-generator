package org.omsk.aispotifyplaylistgenerator.models.spotify.response

import com.fasterxml.jackson.annotation.JsonProperty


data class SpotifyUserDetail(
    @JsonProperty("country") val country: String,
    @JsonProperty("display_name") val displayName: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("explicit_content") val explicitContent: ExplicitContent,
    @JsonProperty("external_urls") val externalUrls: ExternalUrls,
    @JsonProperty("followers") val followers: Followers,
    @JsonProperty("href") val href: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("images") val images: List<Image>,
    @JsonProperty("product") val product: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri") val uri: String
)

data class ExplicitContent(
    @JsonProperty("filter_enabled") val filterEnabled: Boolean,
    @JsonProperty("filter_locked") val filterLocked: Boolean
)

data class ExternalUrls(
    @JsonProperty("spotify") val spotify: String
)

data class Followers(
    @JsonProperty("href") val href: String?,
    @JsonProperty("total") val total: Int
)

data class Image(
    @JsonProperty("url") val url: String,
    @JsonProperty("height") val height: Int?,
    @JsonProperty("width") val width: Int?
)
