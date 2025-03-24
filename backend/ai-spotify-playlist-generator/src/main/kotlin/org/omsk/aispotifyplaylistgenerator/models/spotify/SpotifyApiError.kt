package org.omsk.aispotifyplaylistgenerator.models.spotify

data class SpotifyApiError(
    val error: ErrorDetails
)

data class ErrorDetails(
    val status: Int,
    val message: String
)
