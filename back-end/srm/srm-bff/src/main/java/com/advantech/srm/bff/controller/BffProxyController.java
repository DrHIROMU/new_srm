package com.advantech.srm.bff.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/bff/api")
public class BffProxyController {

  private static final String CLIENT_REGISTRATION_ID = "srm";

  private final WebClient apiWebClient;
  private final OAuth2AuthorizedClientManager authorizedClientManager;

  public BffProxyController(
      @Qualifier("apiWebClient") WebClient apiWebClient,
      OAuth2AuthorizedClientManager authorizedClientManager) {
    this.apiWebClient = apiWebClient;
    this.authorizedClientManager = authorizedClientManager;
  }

  @RequestMapping(
      value = "/**",
      method = {
          RequestMethod.GET,
          RequestMethod.POST,
          RequestMethod.PUT,
          RequestMethod.DELETE,
          RequestMethod.PATCH})
  public ResponseEntity<byte[]> proxy(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication,
      @RequestBody(required = false) byte[] body) {
    return execute(request, response, authentication, body, 0);
  }

  private ResponseEntity<byte[]> execute(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication,
      byte[] body,
      int attempt) {
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    String path = extractPath(request);
    String query = request.getQueryString();
    String uri = query == null ? path : path + "?" + query;

    WebClient.RequestBodySpec requestSpec =
        apiWebClient
            .method(method)
            .uri(uri)
            .headers(headers -> copyHeaders(request, headers));

    WebClient.ResponseSpec responseSpec =
        body != null && body.length > 0 ? requestSpec.bodyValue(body).retrieve() : requestSpec.retrieve();

    try {
      return responseSpec.toEntity(byte[].class).block();
    } catch (WebClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED
          && attempt == 0
          && tryRefresh(request, response, authentication)) {
        return execute(request, response, authentication, body, attempt + 1);
      }
      return ResponseEntity.status(ex.getStatusCode())
          .headers(ex.getHeaders())
          .body(ex.getResponseBodyAsByteArray());
    }
  }

  private boolean tryRefresh(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
            .principal(authentication)
            .attribute(HttpServletRequest.class.getName(), request)
            .attribute(HttpServletResponse.class.getName(), response)
            .build();
    OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
    return authorizedClient != null;
  }

  private String extractPath(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    return requestUri.substring("/bff/api".length());
  }

  private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)
          || HttpHeaders.AUTHORIZATION.equalsIgnoreCase(headerName)) {
        continue;
      }
      headers.add(headerName, request.getHeader(headerName));
    }
  }
}
