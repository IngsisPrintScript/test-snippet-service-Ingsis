package com.ingsis.testService.azureStorageConfig;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Component
public class AssetService {

    private final RestTemplate restTemplate;
    private final String bucketUrl;
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Autowired
    public AssetService(@Value("${bucket.url}") String bucketUrl) {
        this.restTemplate = new RestTemplate();
        this.bucketUrl = bucketUrl;
    }

    public ResponseEntity<String> getSnippet(UUID snippetId) {
        try {
            return restTemplate.getForEntity(bucketUrl + "/" + snippetId.toString(), String.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (Objects.nonNull(correlationId)) {
            headers.set("X-Correlation-Id", correlationId);
        }
        return headers;
    }
}