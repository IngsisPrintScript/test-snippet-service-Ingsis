package com.ingsis.testService.correlationId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;

    @BeforeEach
    void setup() {
        filter = new CorrelationIdFilter();
        MDC.remove("correlationId");
    }

    @Test
    void whenHeaderPresent_putsAndRemovesMDC() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        ServletResponse resp = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("X-Correlation-Id")).thenReturn("my-id");

        doAnswer(invocation -> {
            assertEquals("my-id", MDC.get("correlationId"));
            return null;
        }).when(chain).doFilter(req, resp);

        filter.doFilter(req, resp, chain);

        assertNull(MDC.get("correlationId"));
    }

    @Test
    void whenHeaderMissing_generatesUUIDAndRemovesMDC() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        ServletResponse resp = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("X-Correlation-Id")).thenReturn(null);

        doAnswer(invocation -> {
            Object val = MDC.get("correlationId");
            assertNotNull(val);
            assertTrue(val.toString().length() > 0);
            return null;
        }).when(chain).doFilter(req, resp);

        filter.doFilter(req, resp, chain);

        assertNull(MDC.get("correlationId"));
    }
}
