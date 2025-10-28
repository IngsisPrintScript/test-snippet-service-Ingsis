package com.ingsis.testService.testSnippets;

import com.ingsis.testService.testSnippets.dto.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import java.util.List;
import java.util.UUID;

@Service
public class TestSnippetService {

    private final TestRepo testRepo;
    private static final Logger logger = LoggerFactory.getLogger(TestSnippetService.class);


    public TestSnippetService(TestRepo testRepo) {
        this.testRepo = testRepo;
    }

    @Transactional
    public TestSnippets createTestSnippets(String userId, TestDTO testDTO) {
        logger.info("Creating test for user {}", userId);
        TestSnippets testSnippets = new TestSnippets(userId, testDTO.name(), testDTO.snippetId());
        for (String input : testDTO.input()) {
          logger.info("Input to storage: {}", input);
          testSnippets.getInputs().add(new TestCasesInput(input, testSnippets));
          logger.info("Added input");
        }

        for (String output : testDTO.output()) {
          logger.info("Output to storage: {}", output);
          testSnippets.getExpectedOutputs().add(new TestCaseExpectedOutput(output, testSnippets));
          logger.info("Added expected output to storage");
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
        existing.getInputs().clear();
        existing.getExpectedOutputs().clear();
        for (String input : dto.inputs()) {
          existing.getInputs().add(new TestCasesInput(input, existing));
        }
        for (String output : dto.outputs()) {
          existing.getExpectedOutputs().add(new TestCaseExpectedOutput(output, existing));
        }
        return testRepo.save(existing);
    }

    @Transactional
    public void deleteTest(String testOwner, UUID testId) {
        logger.info("Deleting test {} for user {}", testId, testOwner);
        TestSnippets testToDelete = testRepo.findByIdAndTestOwner(testId, testOwner);
        if (testToDelete == null) {
          logger.error("Test {} not found or not owned by user {}", testId, testOwner);
          throw new RuntimeException("Test not found or not owned by user");
        }
        testRepo.delete(testToDelete);
        testRepo.delete(testToDelete);
        logger.info("Test {} successfully deleted for user {}",testId, testOwner);
    }

    @Transactional
    public void deleteTestsByOwnerAndSnippet(String testOwner, UUID snippetId) {
        logger.info("Deleting all tests for user {} and snippet {}", testOwner, snippetId);
        List<TestSnippets> testsToDelete = testRepo.findAllByTestOwnerAndSnippetId(testOwner, snippetId);
        if (testsToDelete.isEmpty()) {
            logger.warn("No tests found for user {} and snippet {}", testOwner, snippetId);
            return;
        }
        testRepo.deleteAll(testsToDelete);
        logger.info("Successfully deleted {} tests for user {} and snippet {}",
                testsToDelete.size(), testOwner, snippetId);
    }

    public GetTestDTO convertToGetDTO(TestSnippets updated){
        logger.info("Converting inputs test: {}", updated.getInputs());
        List<String> inputs = updated.getInputs().stream()
                .map(TestCasesInput::getInputUrl)
                .toList();
        logger.info("Converting output test: {}", updated.getExpectedOutputs());
        List<String> outputs = updated.getExpectedOutputs().stream()
                .map(TestCaseExpectedOutput::getOutput)
                .toList();
        logger.info("Creating GetTestDTO");
        return new GetTestDTO(
                updated.getSnippetId(),
                updated.getName(),
                inputs,
                outputs
        );
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
                .map(TestCasesInput::getInputUrl)
                .toList();

        List<String> expectedOutputs = testCase.getExpectedOutputs().stream()
                .map(TestCaseExpectedOutput::getOutput)
                .toList();

        List<String> actualOutputs = simulateRun(snippetContent, inputs);

        boolean passed = actualOutputs.equals(expectedOutputs);
        String message = passed
                ? "Test passed"
                : "Expected: " + expectedOutputs + " but got: " + actualOutputs;

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
