package com.ingsis.testService.testSnippets;

import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.dto.*;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestSnippetsController {

  private final TestSnippetService testSnippetService;

  public TestSnippetsController(TestSnippetService testSnippetService) {
    this.testSnippetService = testSnippetService;
  }

  @PostMapping("/create")
  public ResponseEntity<GetTestDTO> testCreateSnippets(
      @AuthenticationPrincipal Jwt jwt, @RequestBody TestDTO testDTO) {
    try {
      TestSnippets created = testSnippetService.createTestSnippets(testDTO);
      GetTestDTO response =
          new GetTestDTO(
              created.getId(),
              created.getSnippetId(),
              created.getName(),
              created.getInputs().stream().map(TestCasesInput::getInputUrl).toList(),
              created.getExpectedOutputs().stream()
                  .map(TestCaseExpectedOutput::getOutput)
                  .toList());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/update")
  public ResponseEntity<GetTestDTO> testUpdateSnippets(
      @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateDTO dto) {
    try {
      TestSnippets updated = testSnippetService.updateTest(dto);
      GetTestDTO response = testSnippetService.convertToGetDTO(updated);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping()
  public ResponseEntity<List<GetTestDTO>> getSnippetTests(
      @AuthenticationPrincipal Jwt jwt, @RequestParam UUID snippetId) {
    try {
      List<GetTestDTO> getTest = testSnippetService.getTestsBySnippetId(snippetId);
      return ResponseEntity.ok(getTest);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{testId}")
  public ResponseEntity<UUID> getSnippetIdByTestId(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID testId) {
    return ResponseEntity.ok(testSnippetService.getTest(testId).getSnippetId());
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteParticularTest(
      @AuthenticationPrincipal Jwt jwt, @RequestParam UUID testId) {
    try {
      testSnippetService.deleteTest(testId);
      return ResponseEntity.ok("Test deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error deleting test: " + e.getMessage());
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> deleteTests(
      @AuthenticationPrincipal Jwt jwt, @RequestParam UUID snippetId) {
    try {
      testSnippetService.deleteTestsBySnippet(snippetId);
      return ResponseEntity.ok("Test deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error deleting test: " + e.getMessage());
    }
  }

  @PostMapping("/run")
  public ResponseEntity<TestRunResultDTO> runTestCase(
      @AuthenticationPrincipal Jwt jwt, @RequestBody TestToRunDTO testToRunDTO) {
    return ResponseEntity.ok(testSnippetService.runTestCase(testToRunDTO));
  }
}
