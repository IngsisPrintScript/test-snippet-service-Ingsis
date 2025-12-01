package com.ingsis.testService.testSnippets;

import static org.junit.jupiter.api.Assertions.*;

import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TestSnippetsEntityTest {

    @Test
    void construct_and_modify_inputs_and_outputs() {
        UUID id = UUID.randomUUID();
        UUID snippetId = UUID.randomUUID();

        TestSnippets entity = new TestSnippets(id, "name", snippetId);

        assertEquals(id, entity.getId());
        assertEquals("name", entity.getName());
        assertEquals(snippetId, entity.getSnippetId());

        entity.getInputs().add(new TestCasesInput(UUID.randomUUID(), "in1", entity));
        entity.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "out1", entity));

        assertEquals(1, entity.getInputs().size());
        assertEquals(1, entity.getExpectedOutputs().size());

        entity.setInputs(List.of());
        entity.setExpectedOutputs(List.of());

        assertTrue(entity.getInputs().isEmpty());
        assertTrue(entity.getExpectedOutputs().isEmpty());
    }
}
