package com.ingsis.testService.testSnippets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ingsis.testService.testSnippets.dto.GetTestDTO;
import com.ingsis.testService.testSnippets.dto.UpdateDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TestSnippetsControllerMoreTest {

    @Mock
    private TestSnippetService service;

    @InjectMocks
    private TestSnippetsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateSnippets_returns_bad_request_on_exception() {
        UpdateDTO dto = new UpdateDTO(UUID.randomUUID(), UUID.randomUUID(), "n", List.of(), List.of());
        when(service.updateTest(dto)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<GetTestDTO> resp = controller.testUpdateSnippets(null, dto);

        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void getSnippetTests_handles_empty_list() {
        UUID snippetId = UUID.randomUUID();
        when(service.getTestsBySnippetId(snippetId)).thenReturn(List.of());

        ResponseEntity<List<GetTestDTO>> resp = controller.getSnippetTests(null, snippetId);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    void deleteParticularTest_returns_not_found_on_runtime_exception() {
        UUID testId = UUID.randomUUID();
        doThrow(new RuntimeException("not found")).when(service).deleteTest(testId);

        ResponseEntity<String> resp = controller.deleteParticularTest(null, testId);

        assertEquals(404, resp.getStatusCodeValue());
    }
}
