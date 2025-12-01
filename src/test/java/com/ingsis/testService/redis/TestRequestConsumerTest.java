package com.ingsis.testService.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.testService.redis.dto.TestRequestEvent;
import com.ingsis.testService.redis.dto.TestResultEvent;
import com.ingsis.testService.testSnippets.TestSnippetService;
import com.ingsis.testService.testSnippets.dto.TestRunResultDTO;
import com.ingsis.testService.testSnippets.dto.TestStatus;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.stream.ObjectRecord;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestRequestConsumerTest {

    private TestRequestConsumer consumer;
    private TestSnippetService testSnippetService;
    private TestResultProducer testResultProducer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testSnippetService = mock(TestSnippetService.class);
        testResultProducer = mock(TestResultProducer.class);
        objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate = mock(
                org.springframework.data.redis.core.RedisTemplate.class);

        consumer = new TestRequestConsumer("stream", "group", redisTemplate, testSnippetService, testResultProducer,
                objectMapper);
    }

    @Test
    void onMessage_processes_event_and_publishes_result() throws Exception {
        UUID snippetId = UUID.randomUUID();
        UUID testId = UUID.randomUUID();
        TestRequestEvent ev = new TestRequestEvent("owner", snippetId, testId, "java", "code");

        @SuppressWarnings("unchecked")
        ObjectRecord<String, String> record = mock(ObjectRecord.class);
        when(record.getValue()).thenReturn(objectMapper.writeValueAsString(ev));

        when(testSnippetService.runTestCase(any())).thenReturn(
                new TestRunResultDTO(TestStatus.PASSED, "m", java.util.List.of("o"), java.util.List.of("e")));

        consumer.onMessage(record);
        Thread.sleep(200);

        verify(testResultProducer, atLeastOnce()).publish(any(TestResultEvent.class));
    }
}
