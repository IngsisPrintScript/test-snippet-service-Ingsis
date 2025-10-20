package com.ingsis.testService.testSnippets.cases;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

@Entity
public class TestSnippets {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotBlank
  private String testOwner;

  @NotBlank
  private String name;

  private UUID snippetId;

  @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<TestCasesInput> inputs;

  @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<TestCaseExpectedOutput> expectedOutputs;

  public TestSnippets() {}

  public TestSnippets(
          String testOwner,
          String name,
          UUID snippetId
  ) {
    this.testOwner = testOwner;
    this.name = name;
    this.snippetId = snippetId;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UUID getSnippetId() {
    return snippetId;
  }
  public String getTestOwner() {
    return testOwner;
  }
  public List<TestCasesInput> getInputs() {
    return inputs;
  }

  public List<TestCaseExpectedOutput> getExpectedOutputs() {
    return expectedOutputs;
  }
  public void setInputs(List<TestCasesInput> inputs) { this.inputs = inputs; }
  public void setExpectedOutputs(List<TestCaseExpectedOutput> expectedOutputs) { this.expectedOutputs = expectedOutputs; }
}
