package org.omsk.aispotifyplaylistgenerator.models.spotify.request

data class AccessTokenRequest(
    val grant_type: String = "authorization_code",
    val code: String,
    val redirect_uri: String,
)
