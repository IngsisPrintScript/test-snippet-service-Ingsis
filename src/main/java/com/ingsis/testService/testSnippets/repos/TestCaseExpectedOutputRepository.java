package com.ingsis.testService.testSnippets.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ingsis.testService.testSnippets.cases.TestCaseExpectedOutput;
import com.ingsis.testService.testSnippets.cases.TestSnippets;

import java.util.UUID;

@Repository
public interface TestCaseExpectedOutputRepository extends JpaRepository<TestCaseExpectedOutput, UUID> {

    public void deleteAllByTestSnippet(TestSnippets testSnippet);
}
