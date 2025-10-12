package com.advantech.srm.auth.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "srm.auth.soap")
public class SoapClientProperties {

  private boolean enabled = false;
  private String endpointUri;
  private List<MockUser> mockUsers = new ArrayList<>();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getEndpointUri() {
    return endpointUri;
  }

  public void setEndpointUri(String endpointUri) {
    this.endpointUri = endpointUri;
  }

  public List<MockUser> getMockUsers() {
    return mockUsers;
  }

  public void setMockUsers(List<MockUser> mockUsers) {
    this.mockUsers = mockUsers;
  }

  public static class MockUser {
    private String userId = "internal-user";
    private String email;
    private String password;
    private String displayName = "Advantech Employee";
    private Set<String> roles = new HashSet<>(Set.of("ROLE_INTERNAL"));
    private Set<String> permissions = new HashSet<>();

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public Set<String> getRoles() {
      return roles;
    }

    public void setRoles(Set<String> roles) {
      this.roles = roles;
    }

    public Set<String> getPermissions() {
      return permissions;
    }

    public void setPermissions(Set<String> permissions) {
      this.permissions = permissions;
    }
  }
}
