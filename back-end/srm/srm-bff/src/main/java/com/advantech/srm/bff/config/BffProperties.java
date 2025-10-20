package com.advantech.srm.bff.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "srm.bff")
public class BffProperties {

  private String apiBaseUrl = "http://localhost:8080";
  private String authBaseUrl = "http://localhost:9000";
  private String frontendSuccessRedirect = "http://localhost:4200/auth/callback";
  private String frontendFailureRedirect = "http://localhost:4200/login?error=oauth_failed";
  private final Cors cors = new Cors();

  public String getApiBaseUrl() {
    return apiBaseUrl;
  }

  public void setApiBaseUrl(String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
  }

  public String getAuthBaseUrl() {
    return authBaseUrl;
  }

  public void setAuthBaseUrl(String authBaseUrl) {
    this.authBaseUrl = authBaseUrl;
  }

  public String getFrontendSuccessRedirect() {
    return frontendSuccessRedirect;
  }

  public void setFrontendSuccessRedirect(String frontendSuccessRedirect) {
    this.frontendSuccessRedirect = frontendSuccessRedirect;
  }

  public String getFrontendFailureRedirect() {
    return frontendFailureRedirect;
  }

  public void setFrontendFailureRedirect(String frontendFailureRedirect) {
    this.frontendFailureRedirect = frontendFailureRedirect;
  }

  public Cors getCors() {
    return cors;
  }

  public static class Cors {
    private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:4200"));

    public List<String> getAllowedOrigins() {
      return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
    }
  }
}
