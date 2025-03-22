package org.omsk.aispotifyplaylistgenerator.models.api

data class ApiResponse<T>(
    val data: T?,
    val error: ApiError?
)
