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

@Configuration
public class RedisConfig {
    // Implement the cache manager bean above
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // Use JSON serialization so cached data is human-readable
        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper()));

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(jsonSerializer)
                .entryTtl(Duration.ofMinutes(30));  // TTL = 30 min

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()));
        return template;
    }

    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build(),
            DefaultTyping.EVERYTHING,
            JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }
}
