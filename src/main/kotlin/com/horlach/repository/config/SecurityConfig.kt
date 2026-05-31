package com.horlach.repository.config

import com.horlach.repository.domain.UserRole
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.security.JwtAuthFilter
import com.horlach.repository.security.UserDetailsServiceImpl
import com.horlach.repository.services.AuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.time.Instant

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
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/v1/works/**","/api/v1/work-files/**","/api/v1/specialties/**", "/api/v1/groups/**", "/api/v1/users/**")
                .hasRole(UserRole.ROLE_USER.withoutPrefix)
                .requestMatchers("/api/v1/works/**","/api/v1/work-files/**")
                .hasRole(UserRole.ROLE_SUPERVISOR.withoutPrefix)
                .requestMatchers("/api/v1/specialties/**", "/api/v1/groups/**", "/api/v1/users/**")
                .hasRole(UserRole.ROLE_SUPERVISOR.withoutPrefix)
                .anyRequest().authenticated()
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

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val hierarchy = "${UserRole.ROLE_ADMIN} > ${UserRole.ROLE_SUPERVISOR}\n${UserRole.ROLE_SUPERVISOR} > ${UserRole.ROLE_USER}"
        return RoleHierarchyImpl.fromHierarchy(hierarchy)
    }

    @Bean
    fun initAdmin(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
        @Value($$"${admin.email}")
        adminEmail: String,
        @Value($$"${admin.password}")
        adminPassword: String
    ): CommandLineRunner = CommandLineRunner {


        if (userRepository.findByEmail(adminEmail) == null) {
            val admin = com.horlach.repository.domain.entity.User(
                id = null,
                email = adminEmail,
                passwordHash = passwordEncoder.encode(adminPassword)!!,
                fullName = "System Administrator",
                role = UserRole.ROLE_ADMIN,
                specialties = emptyList(),
                createdAt = Instant.now()
            )
            userRepository.save(admin)
        }
    }
}
