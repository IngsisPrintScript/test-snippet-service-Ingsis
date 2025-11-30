package com.ingsis.testService.testSnippets.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TestCasesInputTest {

  @Test
  void getters_and_setters_work() {
    UUID id = UUID.randomUUID();
    TestSnippets parent = new TestSnippets(id, "name", UUID.randomUUID());

    TestCasesInput input = new TestCasesInput(id, "url", parent);
    assertEquals(id, input.getId());
    assertEquals("url", input.getInputUrl());
    input.setInputUrl("new");
    assertEquals("new", input.getInputUrl());
    assertEquals(parent, input.getTestSnippet());

    TestSnippets other = new TestSnippets(UUID.randomUUID(), "n", UUID.randomUUID());
    input.setTestSnippet(other);
    assertEquals(other, input.getTestSnippet());
  }
}
