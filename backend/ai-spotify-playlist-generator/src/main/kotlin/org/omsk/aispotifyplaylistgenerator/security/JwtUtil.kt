package org.omsk.aispotifyplaylistgenerator.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val secretKey: SecretKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)

    fun generateToken(spotifyAccessToken: String, expiresIn: Long): String {
        return Jwts.builder()
            .setSubject("spotify-auth")
            .claim("spotify_access_token", spotifyAccessToken)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiresIn * 1000))
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getSpotifyAccessToken(token: String): String? {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["spotify_access_token"] as? String
    }
}