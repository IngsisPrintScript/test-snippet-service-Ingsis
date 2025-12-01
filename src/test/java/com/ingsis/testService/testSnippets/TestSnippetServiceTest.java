package com.ingsis.testService.testSnippets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.testService.azureStorageConfig.AssetService;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.dto.GetTestDTO;
import com.ingsis.testService.testSnippets.dto.TestDTO;
import com.ingsis.testService.testSnippets.dto.TestRunResultDTO;
import com.ingsis.testService.testSnippets.dto.TestStatus;
import com.ingsis.testService.testSnippets.dto.TestToRunDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TestSnippetServiceTest {

    @Mock
    private TestRepo testRepo;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private TestSnippetService service;

    private UUID snippetId;

    @BeforeEach
    void setUp() {
        snippetId = UUID.randomUUID();
    }

    @Test
    void createTestSnippets_saves_and_returns_entity() {
        TestDTO dto = new TestDTO(snippetId, "nm", List.of("in1"), List.of("out1"));

        when(testRepo.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));

        TestSnippets created = service.createTestSnippets(dto);

        assertEquals("nm", created.getName());
        assertEquals(1, created.getInputs().size());
        assertEquals(1, created.getExpectedOutputs().size());
        verify(testRepo).saveAndFlush(any());
    }

    @Test
    void runTestCase_passes_when_outputs_match_simulation() {
        UUID testId = UUID.randomUUID();
        TestSnippets test = new TestSnippets(testId, "n", snippetId);
        test.getInputs().add(new TestCasesInput(UUID.randomUUID(), "in1", test));
        test.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "Output for: in1", test));

        when(testRepo.findById(testId)).thenReturn(Optional.of(test));
        when(assetService.getSnippet(snippetId)).thenReturn(ResponseEntity.ok("code"));

        TestRunResultDTO result = service.runTestCase(new TestToRunDTO(testId, snippetId));

        assertEquals(TestStatus.PASSED, result.status());
    }

    @Test
    void runTestCase_throws_when_snippet_empty() {
        UUID testId = UUID.randomUUID();
        TestSnippets test = new TestSnippets(testId, "n", snippetId);
        test.getInputs().add(new TestCasesInput(UUID.randomUUID(), "in1", test));
        test.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), "Output for: in1", test));

        when(testRepo.findById(testId)).thenReturn(Optional.of(test));
        when(assetService.getSnippet(snippetId)).thenReturn(ResponseEntity.ok("  "));

        assertThrows(RuntimeException.class, () -> service.runTestCase(new TestToRunDTO(testId, snippetId)));
    }

    @Test
    void deleteTest_calls_delete_twice_as_implemented() {
        UUID testId = UUID.randomUUID();
        TestSnippets test = new TestSnippets(testId, "n", snippetId);
        when(testRepo.findById(testId)).thenReturn(Optional.of(test));

        service.deleteTest(testId);

        verify(testRepo, times(2)).delete(test);
    }

    @Test
    void deleteTestsBySnippet_deletes_all_when_found() {
        UUID sId = UUID.randomUUID();
        TestSnippets t = new TestSnippets(UUID.randomUUID(), "n", sId);
        when(testRepo.findAllBySnippetId(sId)).thenReturn(List.of(t));

        service.deleteTestsBySnippet(sId);

        verify(testRepo).deleteAll(List.of(t));
    }

    @Test
    void getTest_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(testRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getTest(id));
    }

    @Test
    void deleteTest_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(testRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.deleteTest(id));
    }

    @Test
    void getTestsBySnippetId_returns_empty_list_when_none() {
        UUID sId = UUID.randomUUID();
        when(testRepo.findAllBySnippetId(sId)).thenReturn(List.of());

        List<GetTestDTO> res = service.getTestsBySnippetId(sId);
        assertNotNull(res);
        assertTrue(res.isEmpty());
    }
}
