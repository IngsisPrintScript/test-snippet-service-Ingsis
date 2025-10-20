package testService.testSnippets.cases;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TestCasesInput {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String input;

    @ManyToOne
    @JoinColumn(name = "test_snippet_id")
    private TestSnippets testSnippet;

    public TestCasesInput() {}
    public TestCasesInput(String value, TestSnippets testCase) {
        this.input = value;
        this.testSnippet = testCase;
    }

    public void setTestCase(TestSnippets testCase) {
        this.testSnippet = testCase;
    }
    public TestSnippets getTestCase() {
        return testSnippet;
    }

    public UUID getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String value) {
        this.input = value;
    }


}

