package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.security.UserDetailsServiceImpl
import com.horlach.repository.services.AuthService
import com.horlach.repository.services.UserService
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsServiceImpl,
    private val userService: UserService
): AuthService {

    @Value("\${jwt.secret}")
    lateinit var SECRET_KEY: String

    val jwtExpirationMs: Long = 1000 * 60 * 60 * 24



    fun authenticate(email: String, password: String): UserDetails{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )
        return userDetailsService.loadUserByUsername(email)
    }

    override fun register(registerRequest: RegisterRequest): AuthResponse {
        userService.createUser(registerRequest)
        val user: UserDetails = userDetailsService.loadUserByUsername(registerRequest.email)
        return AuthResponse(generateToken(user))
    }

    override fun login(loginRequest: LoginRequest): AuthResponse {
        val user = authenticate(loginRequest.email, loginRequest.password)

        return AuthResponse(generateToken(user))
    }

    fun generateToken(user: UserDetails): String{
        val claims: Map<String, Any> = mutableMapOf(
            "role" to user.authorities.first().authority!!
        )

        val token: String = Jwts.builder()
            .subject(user.username)
            .claims(claims)
            .signWith(getSigningKey())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .compact()
        return token
    }

    override fun validateToken(token: String): UserDetails {
        val username = extractUsername(token)
        return userDetailsService.loadUserByUsername(username)
    }

    private fun extractUsername(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()

        return claims.getSubject()
    }

    private fun getSigningKey(): SecretKey{
        val keyBytes: ByteArray = SECRET_KEY.encodeToByteArray()
        return SecretKeySpec(keyBytes,"HmacSHA256")
    }
}