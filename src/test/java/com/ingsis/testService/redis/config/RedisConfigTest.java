package com.ingsis.testService.redis.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

class RedisConfigTest {

  @Test
  void redisTemplate_has_serializers() {
    RedisConfig cfg = new RedisConfig();
    RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
    RedisTemplate<String, Object> template = cfg.redisTemplate(factory);
    assertNotNull(template.getKeySerializer());
    assertNotNull(template.getValueSerializer());
  }
}
