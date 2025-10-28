package com.ingsis.testService.testSnippets.cases;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TestCaseExpectedOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String output;

    @ManyToOne
    @JoinColumn(name = "test_snippet_id")
    private TestSnippets testSnippet;

    public TestCaseExpectedOutput() {}
    public TestCaseExpectedOutput(String outputUrl, TestSnippets testCase) {
        this.output = outputUrl;
        this.testSnippet = testCase;
    }
    public UUID getId() {
        return id;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setTestCase(TestSnippets testCase) {
        this.testSnippet = testCase;
    }
    public TestSnippets getTestCase() {
        return testSnippet;
    }

}
