package org.omsk.aispotifyplaylistgenerator.models.spotify.request

data class CreatePlaylistRequest(
    val name: String,
    val description: String,
    val tracksAmount: Short,
    val public: Boolean
)
