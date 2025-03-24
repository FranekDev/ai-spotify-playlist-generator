package org.omsk.aispotifyplaylistgenerator.ai.prompts

class SpotifyPrompt {

    private var _basePrompt: String = ""

    fun basePrompt(): SpotifyPrompt {
        _basePrompt += """
            I will provide to You description of what I like, what I am interested in etc. Based on this information i want You to generate me 10 tracks that will match my interests.
            These tracks should be available on spotify. Return me json reponse like this: ${jsonTracks()}.
            I want to get tracks not only from authors, shows etc that i provided but also others popular or less popular. In Your response i want You to return ONLY json object with tracks.
            Make sure Your response is valid json object. Do not repeat tracks in response. I want each track to be unique. Also diversify artists so i can get to know new ones.
            If my description isn't enough for You to generate any tracks that match my interests, surprise me.
        """.trimIndent()
        return this
    }

    fun withArgs(args: Map<String, String>): SpotifyPrompt {
        if (!args.any()) return this

        _basePrompt += "I see you are feeling ${args["mood"]}. "
        return this
    }

    fun generateTracks(tracksAmount: Short): SpotifyPrompt {
        _basePrompt += "I want You to generate exactly $tracksAmount tracks. No more, no less. Exacly $tracksAmount."
        return this
    }

    fun withDescription(description: String): SpotifyPrompt {
        _basePrompt += "Here is my description: $description. "
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
