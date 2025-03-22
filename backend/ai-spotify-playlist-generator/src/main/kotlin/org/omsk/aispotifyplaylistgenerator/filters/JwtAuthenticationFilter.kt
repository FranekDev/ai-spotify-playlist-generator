package org.omsk.aispotifyplaylistgenerator.filters

import org.omsk.aispotifyplaylistgenerator.security.JwtUtil
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val jwtUtil: JwtUtil) : WebFilter {

    private val excludedPaths = listOf("/token")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val path = exchange.request.uri.path
        if (excludedPaths.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }

        val request = exchange.request
        val authHeader = request.headers.getFirst("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        val jwtToken = authHeader.substring(7)
        if (!jwtUtil.validateToken(jwtToken)) {
            return ServerResponse.status(401).build().flatMap {
                exchange.response.writeWith(Mono.empty())
            }
        }

        return chain.filter(exchange)
    }
}