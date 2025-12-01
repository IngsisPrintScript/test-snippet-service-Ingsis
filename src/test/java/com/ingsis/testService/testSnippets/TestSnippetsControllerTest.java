package com.ingsis.testService.testSnippets;


import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.dto.GetTestDTO;
import com.ingsis.testService.testSnippets.dto.TestDTO;
import com.ingsis.testService.testSnippets.dto.TestToRunDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TestSnippetsControllerTest {

    @Mock
    private TestSnippetService service;

    @InjectMocks
    private TestSnippetsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_returns_ok_on_success() {
        TestDTO dto = new TestDTO(UUID.randomUUID(), "n", List.of("i"), List.of("o"));
        TestSnippets created = new TestSnippets(UUID.randomUUID(), "n", dto.snippetId());
        when(service.createTestSnippets(dto)).thenReturn(created);

        ResponseEntity<GetTestDTO> resp = controller.testCreateSnippets(null, dto);

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
    }

    @Test
    void getSnippetIdByTestId_returns_uuid() {
        UUID testId = UUID.randomUUID();
        UUID snippetId = UUID.randomUUID();
        TestSnippets t = new TestSnippets(testId, "n", snippetId);
        when(service.getTest(testId)).thenReturn(t);

        ResponseEntity<UUID> resp = controller.getSnippetIdByTestId(null, testId);

        assertEquals(snippetId, resp.getBody());
    }

    @Test
    void runTestCase_delegates_to_service() {
        TestToRunDTO dto = new TestToRunDTO(UUID.randomUUID(), UUID.randomUUID());
        when(service.runTestCase(dto)).thenReturn(null);

        ResponseEntity<?> resp = controller.runTestCase(null, dto);

        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void deleteParticularTest_returns_not_found_on_runtime_exception() {
        UUID testId = UUID.randomUUID();
        doThrow(new RuntimeException("boom")).when(service).deleteTest(testId);

        ResponseEntity<String> resp = controller.deleteParticularTest(null, testId);

        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    void deleteTests_returns_ok_when_service_succeeds() {
        UUID snippetId = UUID.randomUUID();
        // no exception
        ResponseEntity<String> resp = controller.deleteTests(null, snippetId);
        assertEquals(200, resp.getStatusCodeValue());
    }
}
