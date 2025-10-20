package com.advantech.srm.bff.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      BffProperties properties,
      OAuth2AuthorizationRequestResolver authorizationRequestResolver)
      throws Exception {

    http.csrf(csrf -> csrf.ignoringRequestMatchers("/bff/**"))
        .cors(cors -> cors.configurationSource(corsConfigurationSource(properties)))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/actuator/health", "/actuator/info", "/bff/auth/login", "/bff/auth/status")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2
                    .authorizationEndpoint(
                        endpoint -> endpoint.authorizationRequestResolver(authorizationRequestResolver))
                    .redirectionEndpoint(
                        redirection -> redirection.baseUri("/oauth/callback"))
                    .successHandler(
                        (request, response, authentication) ->
                            response.sendRedirect(properties.getFrontendSuccessRedirect()))
                    .failureHandler(
                        (request, response, exception) ->
                            response.sendRedirect(properties.getFrontendFailureRedirect())))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                            response.sendError(HttpServletResponse.SC_FORBIDDEN)));

    return http.build();
  }

  @Bean
  public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
      ClientRegistrationRepository clientRegistrationRepository) {
    DefaultOAuth2AuthorizationRequestResolver resolver =
        new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
    return new OAuth2AuthorizationRequestResolver() {
      @Override
      public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request);
        logAuthorizationRequest(authorizationRequest);
        return authorizationRequest;
      }

      @Override
      public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request, clientRegistrationId);
        logAuthorizationRequest(authorizationRequest);
        return authorizationRequest;
      }

      private void logAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest != null && log.isInfoEnabled()) {
          log.info(
              "OAuth2 authorization request resolved: clientId={}, redirectUri={}, scopes={}, additionalParameters={}",
              authorizationRequest.getClientId(),
              authorizationRequest.getRedirectUri(),
              authorizationRequest.getScopes(),
              authorizationRequest.getAdditionalParameters());
        }
      }
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(BffProperties properties) {
    CorsConfiguration configuration = new CorsConfiguration();
    List<String> allowedOrigins = properties.getCors().getAllowedOrigins();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
