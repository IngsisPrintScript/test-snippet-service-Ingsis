package com.ingsis.testService.testSnippets.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TestCaseExpectedOutputTest {

    @Test
    void getters_and_setters_work() {
        UUID id = UUID.randomUUID();
        TestSnippets parent = new TestSnippets(id, "name", UUID.randomUUID());

        TestCaseExpectedOutput o = new TestCaseExpectedOutput(id, "out", parent);
        assertEquals(id, o.getId());
        assertEquals("out", o.getOutput());
        o.setOutput("other");
        assertEquals("other", o.getOutput());
        assertEquals(parent, o.getTestSnippet());

        TestSnippets other = new TestSnippets(UUID.randomUUID(), "n", UUID.randomUUID());
        o.setTestSnippet(other);
        assertEquals(other, o.getTestSnippet());
    }
}
