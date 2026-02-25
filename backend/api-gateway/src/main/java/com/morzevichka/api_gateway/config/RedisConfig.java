package com.morzevichka.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean
    RedisConnectionFactory jedisConnectionFactory(@Qualifier("redisProperties") RedisProperties properties) {
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
