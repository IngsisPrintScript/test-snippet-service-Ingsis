package testService.testSnippets;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
public class TestSnippets {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotBlank private String testOwner;
  @NotBlank private String name;

  @NotBlank private UUID snippetId;

  @NotBlank private String input;

  @NotBlank private String output;

  public TestSnippets() {}

  public TestSnippets(
      String testOwner, String name, String input, String output, UUID snippetOwner) {
    this.name = name;
    this.testOwner = testOwner;
    this.input = input;
    this.output = output;
    this.snippetId = snippetOwner;
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

  public String getInput() {
    return input;
  }

  public String getOutput() {
    return output;
  }
}
