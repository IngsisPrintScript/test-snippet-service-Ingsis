package com.ingsis.testService.testSnippets;

import com.ingsis.testService.testSnippets.dto.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ingsis.testService.storageConfig.StorageService;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.repos.TestCaseExpectedOutputRepository;
import com.ingsis.testService.testSnippets.repos.TestCasesInputRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class TestSnippetService {

  private final TestRepo testRepo;
  private final TestCasesInputRepository testCasesInputRepository;
  private final TestCaseExpectedOutputRepository testCaseExpectedOutput;
  private final StorageService storageService;
  private static final Logger logger = LoggerFactory.getLogger(TestSnippetService.class);


  public TestSnippetService(TestRepo testRepo,TestCasesInputRepository testCasesInputRepository,
                            TestCaseExpectedOutputRepository testCaseExpectedOutput,
                            StorageService storageService) {
    this.testRepo = testRepo;
    this.testCasesInputRepository = testCasesInputRepository;
    this.testCaseExpectedOutput = testCaseExpectedOutput;
    this.storageService = storageService;
  }

  @Transactional
  public TestSnippets createTestSnippets(String userId, TestDTO testDTO) {
    logger.info("Creating test for user {}", userId);
    TestSnippets testSnippets = new TestSnippets(userId, testDTO.name(), testDTO.snippetId());
    for (String input : testDTO.input()) {
      String blobName = "inputs/" + testSnippets.getId() + "/" + UUID.randomUUID() + ".txt";
      String url = storageService.upload("Testing", blobName, input.getBytes(StandardCharsets.UTF_8));
      testSnippets.getInputs().add(new TestCasesInput(url, testSnippets));
    }

    for (String output : testDTO.output()) {
      String blobName = "outputs/" + testSnippets.getId() + "/" + UUID.randomUUID() + ".txt";
      String url = storageService.upload("Testing", blobName, output.getBytes(StandardCharsets.UTF_8));
      testSnippets.getExpectedOutputs().add(new TestCaseExpectedOutput(url, testSnippets));
    }

    logger.info("Uploaded inputs and outputs to Azure for user {}", userId);
    return testRepo.save(testSnippets);
  }

  @Transactional
  public TestSnippets updateTest(String userId, UpdateDTO dto) {
    logger.info("Updating test for user {}", userId);
    TestSnippets existing = testRepo.findByIdAndTestOwner(dto.testId(), userId);
    if (existing == null) {
      throw new RuntimeException("Test not found or not owned by user");
    }

    testCasesInputRepository.deleteAllByTestCase(existing);
    testCaseExpectedOutput.deleteAllByTestCase(existing);

    List<TestCasesInput> newInputs = dto.inputs().stream()
            .map(input -> {
              String blobName = "inputs/" + existing.getId() + "/" + UUID.randomUUID() + ".txt";
              String url = storageService.upload("Testing", blobName, input.getBytes(StandardCharsets.UTF_8));
              return new TestCasesInput(url, existing);
            })
            .toList();

    List<TestCaseExpectedOutput> newOutputs = dto.outputs().stream()
            .map(output -> {
              String blobName = "outputs/" + existing.getId() + "/" + UUID.randomUUID() + ".txt";
              String url = storageService.upload("Testing", blobName, output.getBytes(StandardCharsets.UTF_8));
              return new TestCaseExpectedOutput(url, existing);
            })
            .toList();

    testCasesInputRepository.saveAll(newInputs);
    testCaseExpectedOutput.saveAll(newOutputs);

    existing.setInputs(newInputs);
    existing.setExpectedOutputs(newOutputs);

    return testRepo.save(existing);
  }

  @Transactional
  public TestRunResultDTO runTestCase(String userId, TestToRunDTO testToRunDTO) {
    logger.info("Running TestCase({}) for user {}", testToRunDTO.testCaseId(), userId);

    TestSnippets testCase = testRepo.findById(testToRunDTO.testCaseId())
            .orElseThrow(() -> new RuntimeException("TestCase not found"));

    String snippetContent = testToRunDTO.content();
    if (snippetContent == null || snippetContent.isBlank()) {
      throw new RuntimeException("Snippet content is empty");
    }

    List<String> inputs = testCase.getInputs().stream()
            .map(input -> new String(storageService.download(input.getInput()), StandardCharsets.UTF_8))
            .toList();

    List<String> expectedOutputs = testCase.getExpectedOutputs().stream()
            .map(output -> new String(storageService.download(output.getOutput()), StandardCharsets.UTF_8))
            .toList();

    // Simular ejecución
    List<String> actualOutputs = simulateRun(snippetContent, inputs);

    boolean passed = actualOutputs.equals(expectedOutputs);
    String message = passed ? "Test passed" : "Expected: " + expectedOutputs + " but got: " + actualOutputs;

    return new TestRunResultDTO(
            passed ? TestStatus.PASSED : TestStatus.FAILED,
            message,
            actualOutputs,
            expectedOutputs
    );
  }

  private List<String> simulateRun(String content, List<String> inputs) {
    return inputs.stream()
            .map(input -> "Output for: " + input)
            .toList();
  }
}
