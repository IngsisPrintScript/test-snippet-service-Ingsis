package com.ingsis.testService.testSnippets;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ingsis.testService.testSnippets.cases.TestSnippets;

@Repository
public interface TestRepo extends JpaRepository<TestSnippets, UUID> {
    List<TestSnippets> findAllBySnippetId(UUID snippetId);}
