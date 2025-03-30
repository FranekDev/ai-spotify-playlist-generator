package org.omsk.aispotifyplaylistgenerator.clients.endpoints

object SpotifyEndpoint {
    const val ME = "me"
    const val USER_PLAYLISTS = "users/{user_id}/playlists"
    const val PLAYLIST = "playlists/{playlist_id}"
    const val PLAYLIST_TRACKS = "playlists/{playlist_id}/tracks"

    const val USER_ID_PLACEHOLDER = "{user_id}"
    const val PLAYLIST_ID_PLACEHOLDER = "{playlist_id}"

    const val API_TOKEN = "api/token"
}