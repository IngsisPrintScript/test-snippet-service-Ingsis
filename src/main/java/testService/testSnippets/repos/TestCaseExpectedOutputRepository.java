package testService.testSnippets.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testService.testSnippets.cases.TestCaseExpectedOutput;
import testService.testSnippets.cases.TestSnippets;

import java.util.UUID;

@Repository
public interface TestCaseExpectedOutputRepository extends JpaRepository<TestCaseExpectedOutput, UUID> {

    public void deleteAllByTestCase(TestSnippets testCase);
}
