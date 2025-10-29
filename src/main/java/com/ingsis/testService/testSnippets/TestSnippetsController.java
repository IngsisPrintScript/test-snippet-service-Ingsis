package com.ingsis.testService.testSnippets;

import com.ingsis.testService.testSnippets.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestSnippetsController {

  private final TestSnippetService testSnippetService;

  public TestSnippetsController(TestSnippetService testSnippetService) {
    this.testSnippetService = testSnippetService;
  }

  @PostMapping("/create")
  public ResponseEntity<GetTestDTO> testCreateSnippets(
          @RequestParam String userId,
          @RequestBody TestDTO testDTO) {
    try {
      TestSnippets created = testSnippetService.createTestSnippets(userId, testDTO);
      GetTestDTO response = new GetTestDTO(
              created.getId(),
              created.getSnippetId(),
              created.getName(),
              created.getInputs().stream()
                      .map(TestCasesInput::getInputUrl)
                      .toList(),
              created.getExpectedOutputs().stream()
                      .map(TestCaseExpectedOutput::getOutput)
                      .toList()
      );
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/update")
  public ResponseEntity<GetTestDTO> testUpdateSnippets(
          @RequestParam String userId,
          @RequestBody UpdateDTO dto) {
    try {
      TestSnippets updated = testSnippetService.updateTest(userId, dto);
      GetTestDTO response = testSnippetService.convertToGetDTO(updated);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/getBySnippet")
  public ResponseEntity<List<GetTestDTO>> getBySnippet(@RequestParam String userId,@RequestParam UUID snippetId){
    try {
      List<GetTestDTO> getTest = testSnippetService.getTestsBySnippetIdAndTestOwner(userId,snippetId);
      return ResponseEntity.ok(getTest);
    }catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/getSnippetTests")
  public ResponseEntity<List<GetTestDTO>> getSnippetTests(@RequestParam UUID snippetId){
    try {
      List<GetTestDTO> getTest = testSnippetService.getTestsBySnippetId(snippetId);
      return ResponseEntity.ok(getTest);
    }catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
  @DeleteMapping()
  public ResponseEntity<String> deleteParticularTest(
          @RequestParam String userId,
          @RequestParam UUID testId) {
    try {
      testSnippetService.deleteTest(userId, testId);
      return ResponseEntity.ok("Test deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error deleting test: " + e.getMessage());
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> deleteTests(
          @RequestParam String userId,
          @RequestParam UUID snippetId) {
    try {
      testSnippetService.deleteTestsByOwnerAndSnippet(userId, snippetId);
      return ResponseEntity.ok("Test deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error deleting test: " + e.getMessage());
    }
  }

  @PostMapping("/run")
  public ResponseEntity<TestRunResultDTO> runTestCase(
          @RequestParam String userId,
          @RequestBody TestToRunDTO testToRunDTO) {
    return ResponseEntity.ok(testSnippetService.runTestCase(userId, testToRunDTO));
  }
}
