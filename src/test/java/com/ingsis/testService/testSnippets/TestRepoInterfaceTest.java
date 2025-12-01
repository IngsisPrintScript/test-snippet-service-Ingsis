package com.ingsis.testService.testSnippets;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRepoInterfaceTest {

    @Test
    void test_is_interface() {
        assertTrue(TestRepo.class.isInterface());
    }
}
