package com.ingsis.testService.redis.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.ingsis.testService.testSnippets.dto.TestStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  void record_fields_are_accessible() {
    UUID id = UUID.randomUUID();
    Result r = new Result(id, "name", TestStatus.PASSED);
    assertEquals(id, r.testId());
    assertEquals("name", r.name());
    assertEquals(TestStatus.PASSED, r.testStatus());
  }
}
