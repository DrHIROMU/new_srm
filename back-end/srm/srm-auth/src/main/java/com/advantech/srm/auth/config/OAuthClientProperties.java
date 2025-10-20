package com.advantech.srm.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "srm.auth.oauth2")
public class OAuthClientProperties {

  private String clientId = "app-bff";
  private String clientSecret = "bff-secret";
  private List<String> redirectUris = new ArrayList<>(List.of("http://localhost:8081/oauth/callback"));
  private List<String> scopes = new ArrayList<>(List.of("openid", "profile"));
  private int accessTokenTtlMinutes = 15;
  private int refreshTokenTtlHours = 24;
  private String issuer = "http://localhost:9000";

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public List<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public int getAccessTokenTtlMinutes() {
    return accessTokenTtlMinutes;
  }

  public void setAccessTokenTtlMinutes(int accessTokenTtlMinutes) {
    this.accessTokenTtlMinutes = accessTokenTtlMinutes;
  }

  public int getRefreshTokenTtlHours() {
    return refreshTokenTtlHours;
  }

  public void setRefreshTokenTtlHours(int refreshTokenTtlHours) {
    this.refreshTokenTtlHours = refreshTokenTtlHours;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }
}
