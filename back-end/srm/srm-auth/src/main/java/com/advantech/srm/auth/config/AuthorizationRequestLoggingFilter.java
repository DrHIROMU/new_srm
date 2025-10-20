package com.advantech.srm.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationRequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AuthorizationRequestLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isAuthorizeEndpoint(request)) {
      log.info(
          "Authorize request [{} {}]: response_type={}, client_id={}, redirect_uri={}, scope={}, state={}, code_challenge_method={}",
          request.getMethod(),
          request.getRequestURI(),
          request.getParameter("response_type"),
          request.getParameter("client_id"),
          request.getParameter("redirect_uri"),
          request.getParameter("scope"),
          request.getParameter("state"),
          request.getParameter("code_challenge_method"));
      if (log.isDebugEnabled()) {
        request.getParameterMap()
            .forEach(
                (key, value) ->
                    log.debug("Request parameter map entry: {} -> {}", key, Arrays.toString(value)));
        MultiValueMap<String, String> queryParameters =
            UriComponentsBuilder.fromPath(request.getRequestURI())
                .query(request.getQueryString())
                .build()
                .getQueryParams();
        log.debug("Parsed query parameters: {}", queryParameters);
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !isAuthorizeEndpoint(request);
  }

  private boolean isAuthorizeEndpoint(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri != null && uri.startsWith("/oauth2/authorize");
  }
}
