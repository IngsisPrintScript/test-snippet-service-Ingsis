package com.ingsis.testService.redis.dto;

import com.ingsis.testService.redis.TestRequestConsumer;
import com.ingsis.testService.testSnippets.dto.TestStatus;

import java.util.UUID;

public record Result(UUID testId, String name, TestStatus testStatus) {
}
