package com.advantech.srm.auth.config;

import com.advantech.srm.auth.security.SrmUserPrincipal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableConfigurationProperties({OAuthClientProperties.class, SoapClientProperties.class})
public class AuthorizationServerConfig {

  private final OAuthClientProperties clientProperties;

  public AuthorizationServerConfig(OAuthClientProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http,
      RegisteredClientRepository registeredClientRepository,
      OAuth2AuthorizationService authorizationService,
      OAuth2AuthorizationConsentService authorizationConsentService)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();

    RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

    http.securityMatcher(endpointsMatcher)
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
        .csrf((csrf) -> csrf.ignoringRequestMatchers(endpointsMatcher));

    http.with(
        authorizationServerConfigurer,
        (authorizationServer) ->
            authorizationServer
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(authorizationService)
                .authorizationConsentService(authorizationConsentService)
                .oidc(Customizer.withDefaults()));

    http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers("/assets/**", "/actuator/health", "/actuator/info")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .csrf((csrf) -> csrf.ignoringRequestMatchers("/api/**"))
        .formLogin(Customizer.withDefaults())
        .logout(Customizer.withDefaults())
        .cors(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient.Builder builder =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientProperties.getClientId())
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(clientProperties.getAccessTokenTtlMinutes()))
                    .refreshTokenTimeToLive(Duration.ofHours(clientProperties.getRefreshTokenTtlHours()))
                    .reuseRefreshTokens(false)
                    .build())
            .clientSettings(
                org.springframework.security.oauth2.server.authorization.settings.ClientSettings.builder()
                    .requireProofKey(true)
                    .requireAuthorizationConsent(false)
                    .build());

    List<String> redirectUris = clientProperties.getRedirectUris();
    redirectUris.forEach(builder::redirectUri);

    List<String> scopes = clientProperties.getScopes();
    scopes.forEach(builder::scope);

    RegisteredClient client = builder.build();
    return new InMemoryRegisteredClientRepository(client);
  }

  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService() {
    return new InMemoryOAuth2AuthorizationConsentService();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings(OAuthClientProperties properties) {
    AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
    if (properties.getIssuer() != null && !properties.getIssuer().isBlank()) {
      builder.issuer(properties.getIssuer());
    }
    return builder.build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
    return (context) -> {
      if (context.getPrincipal() == null) {
        return;
      }
      Object principal = context.getPrincipal().getPrincipal();
      if (principal instanceof SrmUserPrincipal user) {
        context.getClaims()
            .claim("user_id", user.getId())
            .claim("email", user.getEmail())
            .claim("display_name", user.getDisplayName())
            .claim("supplier", user.isSupplier())
            .claim("roles", user.getRoleCodes())
            .claim("permissions", user.getPermissions());
      }
    };
  }
}
