package com.horlach.repository.config

import com.horlach.repository.domain.dtos.ScientificWorkResponse
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {
    @Bean
    fun redisCacheManager(connectionFactory: RedisConnectionFactory, objectMapper: ObjectMapper): RedisCacheManager {

        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                    StringRedisSerializer()
                )
            )

        val cacheConfigurationMap: MutableMap<String, RedisCacheConfiguration> = HashMap()

        cacheConfigurationMap["work"] = defaultConfig.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                JacksonJsonRedisSerializer(objectMapper,ScientificWorkResponse::class.java)
            )
        )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurationMap)
            .build()
    }
}
