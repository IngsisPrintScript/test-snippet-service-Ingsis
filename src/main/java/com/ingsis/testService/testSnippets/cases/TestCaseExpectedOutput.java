package com.ingsis.testService.testSnippets.cases;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TestCaseExpectedOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String outputUrl;

    @ManyToOne
    @JoinColumn(name = "test_snippet_id")
    private TestSnippets testSnippet;

    public TestCaseExpectedOutput() {}
    public TestCaseExpectedOutput(String outputUrl, TestSnippets testCase) {
        this.outputUrl = outputUrl;
        this.testSnippet = testCase;
    }
    public UUID getId() {
        return id;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String output) {
        this.outputUrl = output;
    }

    public void setTestCase(TestSnippets testCase) {
        this.testSnippet = testCase;
    }
    public TestSnippets getTestCase() {
        return testSnippet;
    }

}
