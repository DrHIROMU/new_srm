package com.advantech.srm.api.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class SecurityConfigTest {

  @Test
  void jwtAuthenticationConverterShouldExposeRolesAndPermissions() {
    SecurityConfig config = new SecurityConfig();
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("roles", List.of("ROLE_SUPPLIER"))
            .claim("permissions", List.of("vendors.read"))
            .build();

    JwtAuthenticationToken authentication = (JwtAuthenticationToken) converter.convert(jwt);
    assertThat(authentication).isNotNull();
    assertThat(authentication.getAuthorities())
        .extracting("authority")
        .containsExactlyInAnyOrder("ROLE_SUPPLIER", "vendors.read");
  }
}
