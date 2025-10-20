package com.advantech.srm.api.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

  @GetMapping("/public/health")
  public Map<String, Object> health() {
    return Map.of(
        "status", "UP",
        "timestamp", Instant.now().toString());
  }

  @GetMapping("/orders")
  public Map<String, Object> orders(JwtAuthenticationToken authentication) {
    String owner = authentication.getToken().getSubject();
    return Map.of(
        "orders",
        List.of(
            Map.of(
                "id", "order-001",
                "owner", owner)));
  }
}
