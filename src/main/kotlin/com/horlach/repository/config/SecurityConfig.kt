package com.horlach.repository.config

import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.security.JwtAuthFilter
import com.horlach.repository.security.UserDetailsServiceImpl
import com.horlach.repository.services.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig {

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager{
        return config.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthFilter: JwtAuthFilter): SecurityFilterChain? {
        http
            .authorizeHttpRequests { auth -> auth
                .anyRequest().permitAll()
            }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.disable() }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService = UserDetailsServiceImpl(userRepository)

    @Bean
    fun jwtAuthFilter(authService: AuthService) : JwtAuthFilter = JwtAuthFilter(authService)

}