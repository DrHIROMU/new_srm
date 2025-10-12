package com.advantech.srm.auth.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SoapAuthenticationResponse {

  private final String userId;
  private final String email;
  private final String displayName;
  private final Set<String> roles;
  private final Set<String> permissions;

  public SoapAuthenticationResponse(
      String userId, String email, String displayName, Set<String> roles, Set<String> permissions) {
    this.userId = userId;
    this.email = email;
    this.displayName = displayName;
    this.roles = new HashSet<>(roles);
    this.permissions = new HashSet<>(permissions);
  }

  public String getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<String> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  public Set<String> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }
}
