package com.horlach.repository.ratelimit

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class FixedWindowRateLimiter(
    private val stringRedisTemplate: StringRedisTemplate
){

    fun allowRequest(
        user:   String,
        limit:  Int,
        window: Duration
    ): Boolean{
        val index: Long = System.currentTimeMillis() / window.toMillis()
        val key:   String = "ratelimit:${user}:${index}";

        val rate: Long = stringRedisTemplate.opsForValue().increment(key)

        if (rate == 1L){
            stringRedisTemplate.expire(key, window)
        }

        return rate <= limit
    }

}