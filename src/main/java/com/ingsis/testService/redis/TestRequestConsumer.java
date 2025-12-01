package com.ingsis.testService.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.testService.redis.dto.TestRequestEvent;
import com.ingsis.testService.redis.dto.TestResultEvent;
import com.ingsis.testService.testSnippets.TestSnippetService;
import com.ingsis.testService.testSnippets.dto.TestRunResultDTO;
import com.ingsis.testService.testSnippets.dto.TestToRunDTO;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TestRequestConsumer extends RedisStreamConsumer<String> {

    private static final Logger logger = LoggerFactory.getLogger(TestRequestConsumer.class);

    private final TestSnippetService testSnippetService;
    private final TestResultProducer testResultProducer;
    private final ObjectMapper objectMapper;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public TestRequestConsumer(@Value("${redis.streams.testRequest}") String streamName,
            @Value("${redis.groups.test}") String groupName, RedisTemplate<String, String> redisTemplate,
            TestSnippetService testService, TestResultProducer testResultProducer, ObjectMapper objectMapper) {

        super(streamName, groupName, redisTemplate);
        this.testSnippetService = testService;
        this.testResultProducer = testResultProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(@NotNull ObjectRecord<String, String> record) {
        executor.submit(() -> {
            try {
                TestRequestEvent event = objectMapper.readValue(record.getValue(), TestRequestEvent.class);
                logger.info("Processing lint request for Snippet({}) from User({})", event.snippetId().toString(),
                        event.ownerId());

                TestRunResultDTO response = testSnippetService
                        .runTestCase(new TestToRunDTO(event.testId(), event.snippetId()));
                testResultProducer.publish(new TestResultEvent(event.snippetId(), event.testId(), response.status()));
            } catch (Exception ex) {
                logger.error("Error processing lint request: {}", ex.getMessage());
            }
            return null;
        });
    }

    @Override
    public @NotNull StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
        return StreamReceiver.StreamReceiverOptions.builder().pollTimeout(java.time.Duration.ofSeconds(10))
                .targetType(String.class).build();
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

}