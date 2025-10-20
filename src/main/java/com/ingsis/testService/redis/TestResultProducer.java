package com.ingsis.testService.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.testService.redis.dto.TestResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestResultProducer {

    private static final Logger logger = LoggerFactory.getLogger(TestResultProducer.class);

    private final String streamName;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public TestResultProducer(
            @Value("${redis.streams.testResult}") String streamName,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        this.streamName = streamName;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(TestResultEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.opsForStream().add(ObjectRecord.create(streamName, json));
            logger.info("Published LintResultEvent for Snippet({})", event.snippetId().toString());
        } catch (Exception ex) {
            logger.error("Error publishing LintResultEvent: {}", ex.getMessage());
        }
    }
}
