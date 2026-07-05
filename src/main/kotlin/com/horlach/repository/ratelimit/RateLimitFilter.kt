package com.horlach.repository.ratelimit

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RateLimitFilter(
    private val fixedWindowRateLimiter: FixedWindowRateLimiter
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val user: String = request.remoteAddr

        val isAllowed:Boolean = fixedWindowRateLimiter.allowRequest(user,50, Duration.ofMinutes(1))

        if (!isAllowed) {
            response.status = 429
            response.writer.write("Too many requests\n")
            return
        }

        filterChain.doFilter(request, response)
    }
}