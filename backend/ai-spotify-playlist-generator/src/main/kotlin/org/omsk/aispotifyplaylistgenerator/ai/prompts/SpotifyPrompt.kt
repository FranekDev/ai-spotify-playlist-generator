package org.omsk.aispotifyplaylistgenerator.ai.prompts

import org.omsk.aispotifyplaylistgenerator.models.ai.Track

class SpotifyPrompt {

    private var _basePrompt: String = ""

    fun basePrompt(): SpotifyPrompt {
        _basePrompt += """
            Act as a music supervisor.
            I will provide to You description of what I like, what I am interested in etc. Based on this information i want You to generate me 10 tracks that will match my interests.
            These tracks should be available on spotify. Return me json response like this: ${jsonTracks()}.
            I want to get tracks not only from authors, shows etc that i provided but also others popular or less popular.
            I any title or author that You generate contains double quotes, escape convert them to single quotes.
            Make sure Your response is valid json object. In Your response each track should be distinct. Also diversify artists so i can get to know new ones.
        """.trimIndent()
        return this
    }

    fun generateTracks(tracksAmount: Short): SpotifyPrompt {
        _basePrompt += "I want You to generate exactly $tracksAmount different tracks. No more, no less. Exacly $tracksAmount."
        return this
    }

    fun withDescription(description: String): SpotifyPrompt {
        _basePrompt += "Here is my description: $description. "
        return this
    }

    fun differentThan(tracks: List<Track>): SpotifyPrompt {
        _basePrompt += "I want tracks that are different than: ${tracks.joinToString { it.trackName }}. "
        return this
    }

    fun build(): String {
        return _basePrompt
    }

    private fun jsonTracks(): String {
        return """
            {"tracks":[{"track_name": "name", "track_author": "author", "released_at": "released at"}]}
        """.trimIndent()
    }
}
