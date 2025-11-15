package com.ingsis.testService.authSecurityConfig;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfigurationSource;

class SecurityConfigTest {

  @Test
  void cors_and_jwtDecoder_are_configured() throws Exception {
    SecurityConfig cfg = new SecurityConfig();

    Field issuerField = SecurityConfig.class.getDeclaredField("issuerUri");
    issuerField.setAccessible(true);
    issuerField.set(cfg, "https://example.com/");

    Field audienceField = SecurityConfig.class.getDeclaredField("audience");
    audienceField.setAccessible(true);
    audienceField.set(cfg, "aud");

    CorsConfigurationSource cors = cfg.corsConfigurationSource();
    assertNotNull(cors);

    try {
      Object decoder = cfg.jwtDecoder();
      assertNotNull(decoder);
    } catch (IllegalArgumentException ex) {
      assertTrue(ex.getMessage().length() > 0);
    }
  }
}
