package com.ingsis.testService.testSnippets;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ingsis.testService.testSnippets.cases.TestSnippets;

@Repository
public interface TestRepo extends JpaRepository<TestSnippets, UUID> {

    TestSnippets findByIdAndTestOwner(UUID id, String testOwner);
    List<TestSnippets> findAllByTestOwnerAndSnippetId(String testOwner, UUID snippetId);
    List<TestSnippets> findAllBySnippetId(UUID snippetId);
}
