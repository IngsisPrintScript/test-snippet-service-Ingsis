package com.ingsis.testService.redis.dto;

import java.util.UUID;

public record TestRequestEvent(
    String ownerId, UUID snippetId, UUID testId, String language, String content) {}
