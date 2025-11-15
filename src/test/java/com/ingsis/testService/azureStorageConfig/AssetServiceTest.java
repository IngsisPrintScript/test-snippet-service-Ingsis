package com.ingsis.testService.azureStorageConfig;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class AssetServiceTest {

  @Test
  void getSnippet_returnsBadRequest_whenRemoteFails() {
    AssetService service = new AssetService("http://nonexistent.invalid");
    ResponseEntity<String> resp = service.getSnippet(UUID.randomUUID());
    assertEquals(400, resp.getStatusCodeValue());
  }
}
