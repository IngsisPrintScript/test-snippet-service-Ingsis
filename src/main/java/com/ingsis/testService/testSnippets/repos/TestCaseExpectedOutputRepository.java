package com.ingsis.testService.testSnippets.repos;

import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseExpectedOutputRepository
    extends JpaRepository<TestCaseExpectedOutput, UUID> {

  public void deleteAllByTestSnippet(TestSnippets testSnippet);
}
