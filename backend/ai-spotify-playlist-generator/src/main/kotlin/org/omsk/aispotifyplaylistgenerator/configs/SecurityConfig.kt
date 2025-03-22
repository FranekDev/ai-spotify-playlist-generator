package org.omsk.aispotifyplaylistgenerator.configs

import org.omsk.aispotifyplaylistgenerator.configs.properties.SpotifyProperties
import org.omsk.aispotifyplaylistgenerator.filters.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector
import org.springframework.security.oauth2.server.resource.introspection.SpringReactiveOpaqueTokenIntrospector
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.web.server.invoke
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    val spotifyProperties: SpotifyProperties,
    val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange {
                authorize(anyExchange, permitAll)
                //authorize(anyExchange, authenticated)
            }
//            oauth2ResourceServer {
//                opaqueToken { }
//            }
            csrf { disable() }

            addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

        }
    }

    @Bean
    fun opaqueTokenIntrospector(): ReactiveOpaqueTokenIntrospector {
        return SpringReactiveOpaqueTokenIntrospector(
            "https://accounts.spotify.com/api/token", spotifyProperties.clientId, spotifyProperties.clientSecret
        )
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return source
    }

}