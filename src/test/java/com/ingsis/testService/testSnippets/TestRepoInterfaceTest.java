package com.ingsis.testService.testSnippets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestRepoInterfaceTest {

    @Test
    void test_is_interface() {
        assertTrue(TestRepo.class.isInterface());
    }
}
