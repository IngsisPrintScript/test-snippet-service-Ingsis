package com.ingsis.testService.authSecurityConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidatorTest {

    @Test
    void validate_success_whenAudienceMatches() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(List.of("my-audience"));
        AudienceValidator validator = new AudienceValidator("my-audience");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors() == false);
    }

    @Test
    void validate_failure_whenAudienceMissing() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(List.of("other-aud"));
        AudienceValidator validator = new AudienceValidator("required-aud");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors());
    }
}
