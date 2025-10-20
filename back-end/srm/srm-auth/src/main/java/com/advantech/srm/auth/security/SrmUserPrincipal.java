package com.advantech.srm.auth.security;

import com.advantech.srm.auth.service.SoapAuthenticationResponse;
import com.advantech.srm.persistence.entity.main.auth.UserAccountEntity;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SrmUserPrincipal implements Principal {

  private final String id;
  private final String email;
  private final String displayName;
  private final boolean supplier;
  private final List<GrantedAuthority> authorities;
  private final Set<String> roleCodes;
  private final Set<String> permissions;

  private SrmUserPrincipal(
      String id,
      String email,
      String displayName,
      boolean supplier,
      Collection<String> roleCodes,
      Collection<String> permissions) {
    this.id = id;
    this.email = email;
    this.displayName = displayName;
    this.supplier = supplier;
    this.roleCodes = new HashSet<>(roleCodes);
    this.permissions = new HashSet<>(permissions);
    this.authorities =
        new ArrayList<>(
            this.roleCodes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
    this.permissions.forEach(permission -> this.authorities.add(new SimpleGrantedAuthority(permission)));
  }

  public static SrmUserPrincipal fromSupplier(UserAccountEntity account) {
    String displayName =
        account.getName() != null && !account.getName().isBlank() ? account.getName() : account.getEmail();
    return new SrmUserPrincipal(
        account.getId() != null ? account.getId().toString() : account.getEmail(),
        account.getEmail(),
        displayName,
        true,
        Set.of("ROLE_SUPPLIER"),
        Collections.emptySet());
  }

  public static SrmUserPrincipal fromEmployee(SoapAuthenticationResponse response) {
    return new SrmUserPrincipal(
        response.getUserId(),
        response.getEmail(),
        response.getDisplayName(),
        false,
        response.getRoles(),
        response.getPermissions());
  }

  public static SrmUserPrincipal fromUserDetails(UserDetails userDetails) {
    Set<String> roleAuthorities =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("ROLE_"))
            .collect(Collectors.toSet());
    if (roleAuthorities.isEmpty()) {
      roleAuthorities = Set.of("ROLE_SUPPLIER");
    }
    Set<String> permissions =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> !authority.startsWith("ROLE_"))
            .collect(Collectors.toSet());
    return new SrmUserPrincipal(
        userDetails.getUsername(),
        userDetails.getUsername(),
        userDetails.getUsername(),
        true,
        roleAuthorities,
        permissions);
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean isSupplier() {
    return supplier;
  }

  public Collection<GrantedAuthority> getAuthorities() {
    return Collections.unmodifiableList(authorities);
  }

  public Set<String> getRoleCodes() {
    return Collections.unmodifiableSet(roleCodes);
  }

  public Set<String> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }

  @Override
  public String getName() {
    return email;
  }
}


