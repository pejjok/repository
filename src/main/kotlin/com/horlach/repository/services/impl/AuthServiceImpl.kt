package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.domain.entity.RefreshToken
import com.horlach.repository.repositories.RefreshTokenRepository
import com.horlach.repository.repositories.UserRepository
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
import java.time.Instant

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsServiceImpl,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
): AuthService {

    @Value("\${jwt.secret}")
    lateinit var SECRET_KEY: String

    val jwtExpirationMs: Long = 1000 * 60 * 60 * 24 // 24 hours

    val refreshTokenExpirationMs: Long = 1000 * 60 * 60 * 24 * 30 // 30 days



    fun authenticate(email: String, password: String): UserDetails{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )
        return userDetailsService.loadUserByUsername(email)
    }

    override fun register(registerRequest: RegisterRequest): AuthResponse {
        userService.createUser(registerRequest)
        val user: UserDetails = userDetailsService.loadUserByUsername(registerRequest.email)
        val accessToken = generateToken(user)
        val refreshToken = generateRefreshToken(user.username)
        return AuthResponse(accessToken, refreshToken)
    }

    override fun login(loginRequest: LoginRequest): AuthResponse {
        val user = authenticate(loginRequest.email, loginRequest.password)
        val accessToken = generateToken(user)
        val refreshToken = generateRefreshToken(user.username)
        return AuthResponse(accessToken, refreshToken)
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

    fun generateRefreshToken(email: String): UUID{
        val user = userRepository.findByEmail(email)!!
        val token = UUID.randomUUID()
        val refreshToken = RefreshToken(
            id = null,
            user = user,
            token = token,
            expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs),
            createdAt = Instant.now()
        )
        refreshTokenRepository.save(refreshToken)
        return token
    }

    private fun validateRefreshToken(token: UUID): Boolean {
        val refreshToken = refreshTokenRepository.findById(token).orElse(null) ?: return false
        return refreshToken.expiresAt.isAfter(Instant.now())
    }

    override fun refreshToken(refreshToken: UUID): AuthResponse {
        val tokenEntity = refreshTokenRepository.findById(refreshToken)
            .orElseThrow { IllegalArgumentException("Refresh token not found") }

        if (!validateRefreshToken(refreshToken)) {
            refreshTokenRepository.delete(tokenEntity)
            throw IllegalArgumentException("Refresh token expired or invalid")
        }

        tokenEntity.token = UUID.randomUUID()
        tokenEntity.expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs)
        refreshTokenRepository.save(tokenEntity)

        val userDetails = userDetailsService.loadUserByUsername(tokenEntity.user.email)
        val newAccessToken = generateToken(userDetails)

        return AuthResponse(newAccessToken, tokenEntity.token)
    }
}