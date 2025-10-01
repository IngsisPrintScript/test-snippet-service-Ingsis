package testService.testSnippets;

import org.springframework.stereotype.Service;

@Service
public class TestSnippetService {

  private final TestRepo testRepo;

  public TestSnippetService(TestRepo testRepo) {
    this.testRepo = testRepo;
  }

  public TestSnippets createTestSnippets(String userId, TestDTO testDTO) {
    return testRepo.save(
        new TestSnippets(
            userId,
            testDTO.getName(),
            testDTO.getInput(),
            testDTO.getOutput(),
            testDTO.getSnippetId()));
  }
}
