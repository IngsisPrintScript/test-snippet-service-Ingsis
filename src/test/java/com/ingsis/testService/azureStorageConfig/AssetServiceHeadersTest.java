package com.ingsis.testService.azureStorageConfig;



import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetServiceHeadersTest {

    @Test
    void getHeaders_includes_correlation_when_present() throws Exception {
        AssetService svc = new AssetService("http://example.invalid");

        MDC.put("correlationId", "my-cid");
        try {
            Method m = AssetService.class.getDeclaredMethod("getHeaders");
            m.setAccessible(true);
            Object headers = m.invoke(svc);
            String val = (String) headers.getClass().getMethod("getFirst", String.class).invoke(headers,
                    "X-Correlation-Id");
            assertEquals("my-cid", val);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
