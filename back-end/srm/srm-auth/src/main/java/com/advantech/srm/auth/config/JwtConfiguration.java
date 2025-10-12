package com.advantech.srm.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class JwtConfiguration {

  private final JWKSet jwkSet;

  public JwtConfiguration() {
    this.jwkSet = new JWKSet(generateRsa());
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    return (selector, securityContext) -> selector.select(jwkSet);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
  }

  private static RSAKey generateRsa() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
      return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("srm-auth-signing-key").build();
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to generate RSA key pair for JWT signing", ex);
    }
  }
}
