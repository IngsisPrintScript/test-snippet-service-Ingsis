package com.ingsis.testService.redis.dto;

import com.ingsis.testService.testSnippets.dto.TestStatus;
import java.util.UUID;

public record TestResultEvent(UUID testId, UUID snippetId, TestStatus status) {}
