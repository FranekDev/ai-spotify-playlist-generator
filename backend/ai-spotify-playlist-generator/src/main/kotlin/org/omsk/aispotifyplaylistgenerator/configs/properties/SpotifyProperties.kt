package org.omsk.aispotifyplaylistgenerator.configs.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.spotify")
class SpotifyProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
}
