package testService.testSnippets.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testService.testSnippets.cases.TestCasesInput;
import testService.testSnippets.cases.TestSnippets;

import java.util.UUID;

@Repository
public interface TestCasesInputRepository extends JpaRepository<TestCasesInput, UUID> {

    public void deleteAllByTestCase(TestSnippets testCase);
}
