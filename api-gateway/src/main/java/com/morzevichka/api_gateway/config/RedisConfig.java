package com.morzevichka.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableRedisHttpSession
public class RedisConfig {

    private final RedisProperties properties;

    @Bean
    RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());

        if (Objects.nonNull(properties.getPassword())) {
            if (!properties.getPassword().isBlank()) {
                config.setPassword(properties.getPassword());
            }
        }

        return new JedisConnectionFactory(config);
    }
}
