package com.example.lab05.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// TODO (Section 3 — Redis):
// 1. Add @EnableCaching to this class.
// 2. Create a @Bean method that returns a RedisCacheManager:
//
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
//
//        // Use JSON serialization so cached data is human-readable
//        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
//            RedisSerializationContext.SerializationPair.fromSerializer(
//                new GenericJackson2JsonRedisSerializer());
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(jsonSerializer)
//                .entryTtl(Duration.ofMinutes(30));  // TTL = 30 min
//
//        return RedisCacheManager.builder(factory)
//                .cacheDefaults(config)
//                .build();
//    }
//
// Without @EnableCaching, all @Cacheable annotations are silently ignored!

// Marks this class as a Spring configuration class that provides bean definitions
@Configuration
public class RedisConfig {

    // Defines a Spring-managed bean for the RedisCacheManager (used by @Cacheable, @CacheEvict, etc.)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        // Create a JSON serializer pair for cache values, using our custom ObjectMapper
        // This ensures cached data is stored as human-readable JSON in Redis (not binary)
        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper()));

        // Build the default cache config:
        // - serializeValuesWith: use JSON to serialize cache values
        // - entryTtl: cached entries expire after 30 minutes automatically
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(jsonSerializer)
                .entryTtl(Duration.ofMinutes(30));

        // Build and return the cache manager using the connection factory and default config
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    // Defines a Spring-managed bean for RedisTemplate (used for direct/manual Redis operations)
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate<>();

        // Bind this template to the Redis server via the connection factory
        template.setConnectionFactory(factory);

        // Serialize Redis keys as plain strings (e.g., "product:42") for readability
        template.setKeySerializer(new StringRedisSerializer());

        // Serialize Redis values as JSON using our custom ObjectMapper (with type info)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()));

        return template;
    }

    // Creates a custom ObjectMapper shared by both the cache manager and the RedisTemplate
    // to ensure consistent serialization/deserialization of cached objects
    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register the JavaTimeModule so Jackson can handle Java 8 date/time types
        // (LocalDateTime, Instant, etc.) — without this, date fields cause errors
        mapper.registerModule(new JavaTimeModule());

        // Write dates as ISO strings ("2026-03-23T10:15:30") instead of numeric timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Embed a "@class" type property in every serialized JSON value, e.g.:
        //   {"@class": "com.example.lab05.model.Product", "name": "Laptop", ...}
        // This allows Jackson to deserialize back to the correct Java type.
        // Without this, Redis would return a LinkedHashMap instead of the actual object,
        // causing ClassCastException at runtime.
        mapper.activateDefaultTyping(
            // Allow all classes rooted at Object to be included in type info
            BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build(),
            // EVERYTHING = include type info for all values (not just abstract/interface types)
            DefaultTyping.EVERYTHING,
            // Store the type identifier as a JSON property ("@class") inside the object
            JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }
}
