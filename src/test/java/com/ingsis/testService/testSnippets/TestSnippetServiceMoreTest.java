package com.ingsis.testService.testSnippets;


import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.dto.GetTestDTO;
import com.ingsis.testService.testSnippets.dto.UpdateDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestSnippetServiceMoreTest {

    @Mock
    private TestRepo testRepo;

    @InjectMocks
    private TestSnippetService service;

    private UUID snippetId;

    @BeforeEach
    void setUp() {
        snippetId = UUID.randomUUID();
    }

    @Test
    void updateTest_updates_inputs_and_outputs_when_exists() {
        UUID testId = UUID.randomUUID();
        TestSnippets existing = new TestSnippets(testId, "n", snippetId);
        existing.getInputs().add(new TestCasesInput(UUID.randomUUID(), "old", existing));
        existing.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "oldOut", existing));

        when(testRepo.findById(testId)).thenReturn(Optional.of(existing));
        when(testRepo.save(existing)).thenReturn(existing);

        UpdateDTO dto = new UpdateDTO(testId, snippetId, "n", List.of("inA", "inB"), List.of("outA"));

        TestSnippets updated = service.updateTest(dto);

        assertEquals(2, updated.getInputs().size());
        assertEquals(1, updated.getExpectedOutputs().size());
        verify(testRepo).save(existing);
    }

    @Test
    void updateTest_throws_when_not_found() {
        UUID testId = UUID.randomUUID();
        when(testRepo.findById(testId)).thenReturn(Optional.empty());

        UpdateDTO dto = new UpdateDTO(testId, snippetId, "n", List.of(), List.of());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.updateTest(dto));
    }

    @Test
    void getTestsBySnippetId_returns_converted_dtos_when_found() {
        UUID testId = UUID.randomUUID();
        TestSnippets t = new TestSnippets(testId, "n", snippetId);
        t.getInputs().add(new TestCasesInput(UUID.randomUUID(), "i1", t));
        t.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "o1", t));

        when(testRepo.findAllBySnippetId(snippetId)).thenReturn(List.of(t));

        List<GetTestDTO> result = service.getTestsBySnippetId(snippetId);

        assertEquals(1, result.size());
        assertEquals(List.of("i1"), result.get(0).inputs());
        assertEquals(List.of("o1"), result.get(0).outputs());
    }

    @Test
    void convertToGetDTO_maps_entity_to_dto() {
        UUID testId = UUID.randomUUID();
        TestSnippets t = new TestSnippets(testId, "nm", snippetId);
        t.getInputs().add(new TestCasesInput(UUID.randomUUID(), "inZ", t));
        t.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "outZ", t));

        GetTestDTO dto = service.convertToGetDTO(t);

        assertEquals(testId, dto.testId());
        assertEquals(snippetId, dto.snippetId());
        assertEquals("nm", dto.name());
        assertEquals(List.of("inZ"), dto.inputs());
        assertEquals(List.of("outZ"), dto.outputs());
    }
}
