package com.advantech.srm.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${srm.bff.auth-base-url}")
    private String authBaseUrl;

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
      return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public WebClient apiWebClient(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository authorizedClientRepository,
        BffProperties properties) {
      ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
          new ServletOAuth2AuthorizedClientExchangeFilterFunction(
              clientRegistrationRepository, authorizedClientRepository);
      oauth2.setDefaultClientRegistrationId("srm");
      oauth2.setDefaultOAuth2AuthorizedClient(true);

      return WebClient.builder()
          .baseUrl(properties.getApiBaseUrl())
          .apply(oauth2.oauth2Configuration())
          .exchangeStrategies(
              ExchangeStrategies.builder()
                  .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                  .build())
          .build();
    }

    @Bean
    public WebClient authWebClient(BffProperties properties) {
      return WebClient.builder()
          .baseUrl(authBaseUrl)
          .exchangeStrategies(
              ExchangeStrategies.builder()
                  .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                  .build())
          .build();
    }
}
