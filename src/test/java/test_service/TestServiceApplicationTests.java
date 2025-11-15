package test_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Disabled in unit test run to avoid starting full Spring context during unit testing phase
@Disabled("Disabled during unit test runs that focus on small-unit tests")
@SpringBootTest
class TestServiceApplicationTests {

  @Test
  void contextLoads() {}
}
