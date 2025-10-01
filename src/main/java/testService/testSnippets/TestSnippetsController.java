package testService.testSnippets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestSnippetsController {

  private final TestSnippetService testSnippetService;

  public TestSnippetsController(TestSnippetService testSnippetService) {
    this.testSnippetService = testSnippetService;
  }

  @PostMapping()
  public ResponseEntity<?> testSnippets(@RequestParam String userId, @RequestBody TestDTO testDTO) {
    try {
      return ResponseEntity.ok(testSnippetService.createTestSnippets(userId, testDTO));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
