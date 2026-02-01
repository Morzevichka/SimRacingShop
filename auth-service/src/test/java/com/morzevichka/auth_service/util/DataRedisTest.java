package com.morzevichka.auth_service.util;

import com.morzevichka.auth_service.config.RedisConfig;
import com.morzevichka.auth_service.config.RedisProperties;
import com.morzevichka.auth_service.service.RedisService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        DataRedisAutoConfiguration.class,
        RedisService.class,
        RedisConfig.class,
        RedisProperties.class
})
public @interface DataRedisTest {
}
