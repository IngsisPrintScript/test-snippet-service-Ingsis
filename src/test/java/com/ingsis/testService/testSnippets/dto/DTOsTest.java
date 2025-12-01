package com.ingsis.testService.testSnippets.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DTOsTest {

    @Test
    void test_records_getters() {
        UUID snippetId = UUID.randomUUID();
        TestDTO t = new TestDTO(snippetId, "n", List.of("i"), List.of("o"));
        assertEquals(snippetId, t.snippetId());
        assertEquals("n", t.name());

        GetTestDTO g = new GetTestDTO(UUID.randomUUID(), snippetId, "n", List.of("i"), List.of("o"));
        assertEquals(snippetId, g.snippetId());

        TestRunResultDTO r = new TestRunResultDTO(TestStatus.PASSED, "m", List.of("a"), List.of("e"));
        assertEquals(TestStatus.PASSED, r.status());

        TestToRunDTO tr = new TestToRunDTO(UUID.randomUUID(), snippetId);
        assertNotNull(tr.testCaseId());
    }
}
