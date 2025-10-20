package com.advantech.srm.bff.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/bff/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private static final String CLIENT_REGISTRATION_ID = "srm";

  private final OAuth2AuthorizedClientRepository authorizedClientRepository;
  private final WebClient authWebClient;

  @Value("${srm.bff.auth-logout-path}")
  private String authLogoutPath;

  @GetMapping("/login")
  public void login(HttpServletResponse response) throws IOException {
    response.sendRedirect("/oauth2/authorization/" + CLIENT_REGISTRATION_ID);
  }

  @GetMapping("/status")
  public ResponseEntity<UserSessionResponse> sessionStatus(Authentication authentication) {
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof OidcUser oidcUser) {
      return ResponseEntity.ok(toResponse(oidcUser));
    }
    if (principal instanceof OAuth2User oauth2User) {
      return ResponseEntity.ok(toResponse(oauth2User.getAttributes()));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    propagateLogoutToAuthorizationServer(request);
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      authorizedClientRepository.removeAuthorizedClient(
          CLIENT_REGISTRATION_ID, authentication, request, response);
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    } else {
      SecurityContextHolder.clearContext();
    }
    return ResponseEntity.noContent().build();
  }

  private void propagateLogoutToAuthorizationServer(HttpServletRequest request) {
    String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
    if (cookieHeader == null || cookieHeader.isBlank()) {
      return;
    }
    try {
      authWebClient
          .post()
          .uri(authLogoutPath)
          .header(HttpHeaders.COOKIE, cookieHeader)
          .retrieve()
          .toBodilessEntity()
          .block(Duration.ofSeconds(5));
    } catch (WebClientResponseException ex) {
      if (log.isWarnEnabled()) {
        log.warn(
            "Authorization server logout returned status {}: {}",
            ex.getStatusCode(),
            ex.getResponseBodyAsString());
      }
    } catch (Exception ex) {
      log.warn("Failed to propagate logout to authorization server", ex);
    }
  }

  private UserSessionResponse toResponse(OidcUser oidcUser) {
    return toResponse(oidcUser.getClaims());
  }

  private UserSessionResponse toResponse(Map<String, Object> claims) {
    String id = stringClaim(claims, "user_id");
    if (id == null) {
      id = stringClaim(claims, "sub");
    }
    String email = stringClaim(claims, "email");
    String displayName = stringClaim(claims, "display_name");
    boolean supplier = Boolean.parseBoolean(String.valueOf(claims.getOrDefault("supplier", false)));
    Set<String> roles = toStringSet(claims.get("roles"));
    Set<String> permissions = toStringSet(claims.get("permissions"));
    return new UserSessionResponse(id, email, displayName, supplier, roles, permissions);
  }

  private String stringClaim(Map<String, Object> claims, String key) {
    Object value = claims.get(key);
    return value != null ? String.valueOf(value) : null;
  }

  @SuppressWarnings("unchecked")
  private Set<String> toStringSet(Object claim) {
    if (claim instanceof Iterable<?> iterable) {
      Set<String> values = new LinkedHashSet<>();
      for (Object element : iterable) {
        if (element != null) {
          values.add(String.valueOf(element));
        }
      }
      return values;
    }
    return Collections.emptySet();
  }

  public record UserSessionResponse(
      String id, String email, String displayName, boolean supplier, Set<String> roles, Set<String> permissions) {}
}
