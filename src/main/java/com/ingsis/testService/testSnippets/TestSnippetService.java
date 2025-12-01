package com.ingsis.testService.testSnippets;

import com.ingsis.testService.azureStorageConfig.AssetService;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestCasesInput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import com.ingsis.testService.testSnippets.dto.GetTestDTO;
import com.ingsis.testService.testSnippets.dto.TestDTO;
import com.ingsis.testService.testSnippets.dto.TestRunResultDTO;
import com.ingsis.testService.testSnippets.dto.TestStatus;
import com.ingsis.testService.testSnippets.dto.TestToRunDTO;
import com.ingsis.testService.testSnippets.dto.UpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestSnippetService {

    private final TestRepo testRepo;
    private final AssetService assetService;
    private static final Logger logger = LoggerFactory.getLogger(TestSnippetService.class);

    public TestSnippetService(TestRepo testRepo, AssetService assetService) {
        this.testRepo = testRepo;
        this.assetService = assetService;
    }

    @Transactional
    public TestSnippets createTestSnippets(TestDTO testDTO) {
        TestSnippets testSnippets = new TestSnippets(UUID.randomUUID(), testDTO.name(), testDTO.snippetId());
        logger.info("Creating test snippets with id {}", testSnippets.getId());
        for (String input : testDTO.input()) {
            logger.info("Input to storage: {}", input);
            testSnippets.getInputs().add(new TestCasesInput(UUID.randomUUID(), input, testSnippets));
            logger.info("Added input");
        }
        for (String output : testDTO.output()) {
            logger.info("Output to storage: {}", output);
            testSnippets.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), output, testSnippets));
            logger.info("Added expected output to storage");
        }
        TestSnippets test = testRepo.saveAndFlush(testSnippets);
        logger.info("Saved test snippets with id {}", testSnippets.getId());
        return test;
    }

    @Transactional
    public TestSnippets updateTest(UpdateDTO dto) {
        TestSnippets existing = testRepo.findById(dto.testId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));
        if (existing == null) {
            throw new RuntimeException("Test not found or not owned by user");
        }
        existing.getInputs().clear();
        existing.getExpectedOutputs().clear();
        for (String input : dto.inputs()) {
            existing.getInputs().add(new TestCasesInput(UUID.randomUUID(), input, existing));
        }
        for (String output : dto.outputs()) {
            existing.getExpectedOutputs().add(new TestCaseExpectedOutput(UUID.randomUUID(), output, existing));
        }
        return testRepo.save(existing);
    }

    @Transactional
    public void deleteTest(UUID testId) {
        logger.info("Deleting test {} ", testId);
        TestSnippets testToDelete = testRepo.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));
        if (testToDelete == null) {
            logger.error("Test {} not found or not owned", testId);
            throw new RuntimeException("Test not found or not owned by user");
        }
        testRepo.delete(testToDelete);
        testRepo.delete(testToDelete);
        logger.info("Test {} successfully deleted", testId);
    }

    @Transactional
    public void deleteTestsBySnippet(UUID snippetId) {
        logger.info("Deleting all tests for snippet {}", snippetId);
        List<TestSnippets> testsToDelete = testRepo.findAllBySnippetId(snippetId);
        if (testsToDelete.isEmpty()) {
            logger.warn("No tests found for snippet {}", snippetId);
            return;
        }
        testRepo.deleteAll(testsToDelete);
        logger.info("Successfully deleted {} tests for snippet {}", testsToDelete.size(), snippetId);
    }

    public List<GetTestDTO> getTestsBySnippetId(UUID snippetId) {
        logger.info("Fetching tests for snippet {}", snippetId);

        List<TestSnippets> tests = testRepo.findAllBySnippetId(snippetId);
        if (tests.isEmpty()) {
            logger.warn("No tests found for snippet {}", snippetId);
            return List.of();
        }

        List<GetTestDTO> result = tests.stream().map(this::convertToGetDTO).toList();

        logger.info("Found {} tests for snippet {}", result.size(), snippetId);
        return result;
    }

    public GetTestDTO convertToGetDTO(TestSnippets updated) {
        logger.info("Converting inputs test: {}", updated.getInputs());
        List<String> inputs = updated.getInputs().stream().map(TestCasesInput::getInputUrl).toList();
        logger.info("Converting output test: {}", updated.getExpectedOutputs());
        List<String> outputs = updated.getExpectedOutputs().stream().map(TestCaseExpectedOutput::getOutput).toList();
        logger.info("Creating GetTestDTO");
        return new GetTestDTO(updated.getId(), updated.getSnippetId(), updated.getName(), inputs, outputs);
    }

    @Transactional
    public TestRunResultDTO runTestCase(TestToRunDTO testToRunDTO) {
        logger.info("Running TestCase({})", testToRunDTO.testCaseId());

        TestSnippets testCase = testRepo.findById(testToRunDTO.testCaseId())
                .orElseThrow(() -> new RuntimeException("TestCase not found"));

        String snippetContent = assetService.getSnippet(testCase.getSnippetId()).getBody();
        if (snippetContent == null || snippetContent.isBlank()) {
            throw new RuntimeException("Snippet content is empty");
        }

        List<String> inputs = testCase.getInputs().stream().map(TestCasesInput::getInputUrl).toList();

        List<String> expectedOutputs = testCase.getExpectedOutputs().stream().map(TestCaseExpectedOutput::getOutput)
                .toList();

        List<String> actualOutputs = simulateRun(inputs);

        boolean passed = actualOutputs.equals(expectedOutputs);
        String message = passed ? "Test passed" : "Expected: " + expectedOutputs + " but got: " + actualOutputs;

        return new TestRunResultDTO(passed ? TestStatus.PASSED : TestStatus.FAILED, message, actualOutputs,
                expectedOutputs);
    }

    private List<String> simulateRun(List<String> inputs) {
        return inputs.stream().map(input -> "Output for: " + input).toList();
    }

    public TestSnippets getTest(UUID testId) {
        return testRepo.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));
    }
}
