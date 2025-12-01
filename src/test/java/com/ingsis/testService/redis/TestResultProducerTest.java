package com.ingsis.testService.redis;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.testService.redis.dto.TestResultEvent;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

class TestResultProducerTest {

  private RedisTemplate<String, String> redisTemplate;
  private StreamOperations<String, String, ObjectRecord<String, String>> streamOps;
  private TestResultProducer producer;

  @BeforeEach
  void setUp() {
    redisTemplate = mock(RedisTemplate.class);
    streamOps = mock(StreamOperations.class);
    when(redisTemplate.opsForStream())
        .thenReturn((org.springframework.data.redis.core.StreamOperations) streamOps);
    producer = new TestResultProducer("stream", redisTemplate, new ObjectMapper());
  }

  @Test
  void publish_writes_to_stream() {
    TestResultEvent ev = new TestResultEvent(UUID.randomUUID(), UUID.randomUUID(), null);
    producer.publish(ev);
    verify(redisTemplate).opsForStream();
  }
}
