package com.ingsis.testService.testSnippets.dto;

import java.util.List;
import java.util.UUID;

public record GetTestDTO(UUID testId, UUID snippetId, String name, List<String> inputs, List<String> outputs) {
}
