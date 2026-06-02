package com.horlach.repository.services.impl

import com.horlach.repository.domain.dtos.AuthResponse
import com.horlach.repository.domain.dtos.ChangePasswordRequest
import com.horlach.repository.domain.dtos.LoginRequest
import com.horlach.repository.domain.dtos.RegisterRequest
import com.horlach.repository.domain.entity.RefreshToken
import com.horlach.repository.domain.entity.User
import com.horlach.repository.repositories.RefreshTokenRepository
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.security.UserDetailsImpl
import com.horlach.repository.security.UserDetailsServiceImpl
import com.horlach.repository.services.AuthService
import com.horlach.repository.services.EmailService
import com.horlach.repository.services.UserService
import io.jsonwebtoken.Claims
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
    private val emailService: EmailService,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${jwt.secret}")
    val SECRET_KEY: String,
    @Value("\${cors.origin}")
    val origin: String
): AuthService {

    val jwtExpirationMs: Long = 1000 * 60 * 60 * 24 // 24 hours

    val refreshTokenExpirationMs: Long = 1000 * 60 * 60 * 24 * 30 // 30 days

    val passwordTokenExpirationMs: Long = 1000 * 60 * 10 // 10 minute



    fun authenticate(email: String, password: String): UserDetailsImpl{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )
        return userDetailsService.loadUserByUsername(email) as UserDetailsImpl
    }

    override fun register(registerRequest: RegisterRequest): AuthResponse {
        userService.createUser(registerRequest)
        val user: UserDetailsImpl = userDetailsService.loadUserByUsername(registerRequest.email) as UserDetailsImpl
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user.getUser())
        return AuthResponse(accessToken, refreshToken)
    }

    override fun login(loginRequest: LoginRequest): AuthResponse {
        val user = authenticate(loginRequest.email, loginRequest.password)
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user.getUser())
        return AuthResponse(accessToken, refreshToken)
    }

    fun generateAccessToken(user: UserDetailsImpl): String{
        val claims: Map<String, Any> = mutableMapOf(
            "role" to user.authorities.first().authority!!,
            "userId" to user.getId()
        )

        return generateToken(user, claims, jwtExpirationMs)
    }

    private fun generateToken(
        user: UserDetailsImpl,
        claims: Map<String, Any>,
        expirationMs: Long
    ): String {
        val token: String = Jwts.builder()
            .subject(user.username)
            .claims(claims)
            .signWith(getSigningKey())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .compact()
        return token
    }

    override fun validateAccessToken(token: String): UserDetails {
        val claims = extractClaims(token)
        return userDetailsService.loadUserByUsername(claims.subject)
    }

    private fun extractClaims(token: String): Claims {
        val claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()

        return claims
    }

    private fun getSigningKey(): SecretKey{
        val keyBytes: ByteArray = SECRET_KEY.encodeToByteArray()
        return SecretKeySpec(keyBytes,"HmacSHA256")
    }

    fun generateRefreshToken(user: User): UUID{
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

        val userDetails = userDetailsService.loadUserByUsername(tokenEntity.user.email) as UserDetailsImpl
        val newAccessToken = generateAccessToken(userDetails)

        return AuthResponse(newAccessToken, tokenEntity.token)
    }

    override fun changePasswordRequest(email: String) {
        val user = userDetailsService.loadUserByUsername(email) as UserDetailsImpl
        generatePasswordToken(user)

        val text = """
            Для зміни паролю перейдіть за посиланням: ${origin}/change-password?token=${generatePasswordToken(user)}
        """.trimIndent()

        emailService.send(text,email)
    }

    private fun generatePasswordToken(user: UserDetailsImpl): String{
        val claims: Map<String, Any> = mutableMapOf(
            "userId" to user.getId()
        )

        return generateToken(user, claims, passwordTokenExpirationMs)
    }

    override fun validatePasswordToken(
        request: ChangePasswordRequest
    ): AuthResponse {
        val claims = extractClaims(request.token)

        userService.changePassword(UUID.fromString(claims["userId"] as String?), request.newPassword)
        val user = userDetailsService.loadUserByUsername(claims.subject) as UserDetailsImpl

        return AuthResponse(generateAccessToken(user), generateRefreshToken(user.getUser()))
    }
}