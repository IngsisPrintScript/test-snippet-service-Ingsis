package com.ingsis.testService.redis.dto;

import com.ingsis.testService.testSnippets.dto.TestStatus;

import java.util.UUID;

public record TestResultEvent(String userId, UUID snippetId, TestStatus status) {
}
