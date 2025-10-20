package testService.testSnippets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import testService.testSnippets.cases.TestCaseExpectedOutput;
import testService.testSnippets.cases.TestCasesInput;
import testService.testSnippets.cases.TestSnippets;
import testService.testSnippets.dto.*;

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
              created.getSnippetId(),
              created.getName(),
              created.getInputs().stream()
                      .map(TestCasesInput::getInput)
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

      GetTestDTO response = new GetTestDTO(
              updated.getSnippetId(),
              updated.getName(),
              updated.getInputs().stream()
                      .map(TestCasesInput::getInput)
                      .toList(),
              updated.getExpectedOutputs().stream()
                      .map(TestCaseExpectedOutput::getOutput)
                      .toList()
      );
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/run")
  public ResponseEntity<TestRunResultDTO> runTestCase(
          @RequestParam String userId,
          @RequestBody TestToRunDTO testToRunDTO) {
    return ResponseEntity.ok(testSnippetService.runTestCase(userId, testToRunDTO));
  }
}
